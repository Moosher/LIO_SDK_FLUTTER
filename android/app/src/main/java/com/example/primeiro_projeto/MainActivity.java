package com.example.primeiro_projeto;

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
import java.util.List;

public class MainActivity extends FlutterActivity implements MethodChannel.MethodCallHandler {

  static final String TAG = "test";
  static final String CHANNEL = "com.example.primeiro_projeto/service";
  protected OrderManager orderManager;
  protected InfoManager infoManager;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);
    new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(this::onMethodCall);
  }

  @Override
  public void onMethodCall(MethodCall call, MethodChannel.Result result) {
      try {
          if (call.method.equals("start")) {
            Log.i(TAG, call.method);
            infoManager = new InfoManager();
            Credentials credentials = new Credentials( "clientID", "accessToken");
            orderManager = new OrderManager(credentials, this);
            Order order = orderManager.createDraftOrder("REFERÊNCIA_DO_PEDIDO");
            order.addItem("324324324324", "coca", 43, 3, "UNIDADE");
            order.addItem("543324324324", "peups", 33, 3, "UNIDADE");

            ResultOrders resultOrders = orderManager.retrieveOrders(200, 0);
            final List<Order> orderList = resultOrders.getResults();
            Log.i(TAG, "orders: " + orderList);
            for (Order or : orderList) {
                Log.i("Order: ", or.getNumber() + " - " + or.getPrice());
            }
            orderManager.placeOrder(order);
            Log.i(TAG, "iru");
          }
      } catch(Exception e){
        Log.i(TAG, e.getMessage());
      } 
  }
}
