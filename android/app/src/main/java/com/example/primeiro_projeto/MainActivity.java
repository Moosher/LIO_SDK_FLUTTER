package com.example.primeiro_projeto;

import java.lang.reflect.Method;
import android.os.Bundle;
import android.util.Log;
import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import cielo.sdk.info.InfoManager;
import cielo.sdk.order.OrderManager;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.DeviceModel;
import cielo.orders.domain.Settings;
import cielo.orders.domain.Order;
import cielo.orders.domain.ResultOrders;
import cielo.sdk.order.ServiceBindListener;

import cielo.orders.domain.CancellationRequest;
import cielo.sdk.order.cancellation.CancellationListener;
import cielo.orders.domain.CheckoutRequest;

import cielo.sdk.order.payment.Payment;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;

import java.util.Set;
import java.util.List;
import java.lang.Throwable;

public class MainActivity extends FlutterActivity implements MethodChannel.MethodCallHandler {

  static final String TAG = "VEK";
  static final String CHANNEL = "com.example.primeiro_projeto/service";
  
  protected OrderManager orderManager;
  protected InfoManager infoManager;
  protected Credentials credentials;
  protected Order order;

  String clienteID = "08zXSSMeGNVFOx2uGzEXqrefyWxVNAXGoSXcT3URzlTLfxipGB";
  String token = "wshEbe6ZOrQKxWdkRWjyqTcjJkP2rWCpM9xLoLFvriV2TOMPLV";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);
    new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(this::onMethodCall);
    infoManager = new InfoManager();
    credentials = new Credentials(clienteID, token);
  }

  private void createOrder(String valor, MethodChannel.Result result) {
    orderManager.bind(this, new ServiceBindListener() {

      @Override
      public void onServiceBoundError(Throwable throwable) {
        Log.i(TAG, throwable.toString());
      }

      @Override
      public void onServiceBound() {
        order = orderManager.createDraftOrder("ORDEM");
        order.addItem("0", "0", Integer.parseInt(valor), 1, "UNIDADE");
        orderManager.updateOrder(order);
        Log.i(TAG, "bound");
        result.success(null);
      }

      @Override
      public void onServiceUnbound() {
        Log.i(TAG, "unbound");
      }
    });
  }

  private void payOrder(MethodChannel.Result result) {
    orderManager.placeOrder(order);
    CheckoutRequest request = new CheckoutRequest.Builder().orderId(order.getId()).amount((int) order.getPrice())
        .build();

    orderManager.checkoutOrder(request, new PaymentListener() {
      @Override
      public void onStart() {
        Log.d("SDKClient", "O pagamento começou.");
      }

      @Override
      public void onPayment(Order order) {
        Log.d("SDKClient", "Um pagamento foi realizado.");

        String returnValue = "Últimos pagamentos:\n";
        for (Payment py : order.getPayments()) {
          String valorRetorno = order.getPrice() + "";
          String centavos = valorRetorno.substring(valorRetorno.length() - 2);
          String reais = valorRetorno.substring(0, valorRetorno.length() - 2);
          String valor = reais + "," + centavos;
          returnValue += "Valor: R$" + valor + " | tipo pagamento: " + py.getPaymentFields().get("primaryProductName");
        }
        result.success(returnValue);
        resetOrder();
      }

      @Override
      public void onCancel() {
        Log.d("SDKClient", "A operação foi cancelada.");
      }

      @Override
      public void onError(PaymentError paymentError) {
        Log.d("SDKClient", "Houve um erro no pagamento.");
      }
    });
  }

  private void resetOrder() {
    order = null;
    orderManager.unbind();
  }

  private void cancelOrder() {
    CancellationRequest request = new CancellationRequest.Builder().orderId(order.getId())
        .authCode(order.getPayments().get(0).getAuthCode()).cieloCode(order.getPayments().get(0).getCieloCode())
        .value(order.getPayments().get(0).getAmount()).build();

    orderManager.cancelOrder(request, new CancellationListener() {
      @Override
      public void onSuccess(Order cancelledOrder) {
        Log.d("SDKClient", "O pagamento foi cancelado.");
      }

      @Override
      public void onCancel() {
        Log.d("SDKClient", "A operação foi cancelada.");
      }

      @Override
      public void onError(PaymentError paymentError) {
        Log.d("SDKClient", "Houve um erro no cancelamento");
      }
    });
  }

  @Override
  public void onMethodCall(MethodCall call, MethodChannel.Result result) {
    try {
      if (call.method.equals("start")) {
        orderManager = new OrderManager(credentials, this);
        this.createOrder(call.argument("valor"), result);
      }

      if (call.method.equals("orders")) {
        ResultOrders resultOrders = orderManager.retrieveOrders(200, 0);
        final List<Order> orderList = resultOrders.getResults();
        Log.i(TAG, "listando");
        for (Order or : orderList) {
          String tmp = or.getId() + " - " + or.getPrice();
          Log.i("Order: ", tmp);
        }
      }

      if (call.method.equals("cancel")) {
        this.cancelOrder();
      }
      if (call.method.equals("pay")) {
        this.payOrder(result);
      }
      if (call.method.equals("destroy")) {
        this.resetOrder();
      }

      if (call.method.equals("methods")) {
        Log.i("ORDER", "ORDER");
        Class tClass = Order.class;
        Method[] methods = tClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
          Log.i("METODO", methods[i].getName());
        }
        Log.i("PAYMENT", "PAYMENT");
        tClass = Payment.class;
        methods = tClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
          Log.i("METODO", methods[i].getName());
        }

      }
    } catch (Exception e) {
      Log.i(TAG, "JAVA ERROR M.A." + e.getMessage());
    }
  }
}
