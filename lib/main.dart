import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MaterialApp(
    title: "Contador de Pedidos",
    home: Home(),
  ));
}

class Home extends StatefulWidget {
  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  String _status = "N/A";
  final control = TextEditingController();
  static const platform =
      const MethodChannel('com.example.primeiro_projeto/service');

  void _addOrder() async{
    try {
      String valor = control.text;
      if(valor.isEmpty){
        return;
      }
      if(valor.contains(",")){
        valor = valor.replaceAll(",", "");
      }else{
        valor += "00";
      }

      await platform.invokeMethod(
          "start", <String, String>{"valor": valor}).then( (r) =>
             this._payOrder()
          );
    } catch (e) {
      print(e);
    }
  }

  void _destroyBind() {
    try {
      platform.invokeMethod("cancel");
    } catch (e) {
      print(e);
    }
  }

  void _getMethods() {
    try {
      platform.invokeMethod("methods");
    } catch (e) {
      print(e);
    }
  }

  void _getOrders() {
    try {
      platform.invokeMethod("orders");
    } catch (e) {
      print(e);
    }
  }
  void _killOrder() {
    try {
      platform.invokeMethod("destroy");
    } catch (e) {
      print(e);
    }
  }

  void _payOrder() async {
    try {
      String valor = await platform.invokeMethod("pay");
      changeStatus(valor);
    } catch (e) {
      print(e);
    }
  }

  void changeStatus(String valor) {
    setState(() {
      _status = valor;
      control.text = "";
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("VEK PAY"),
      ),
      body: SingleChildScrollView(
          padding: EdgeInsets.all(10),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: <Widget>[
              TextField(
                keyboardType: TextInputType.number,
                controller: control,
                decoration: InputDecoration(labelText: "Valor"),
              ),
              Divider(),
              // AbsorbPointer(
              //   absorbing: false,
              //   child:             RaisedButton(
              //   child: Text("ADICIONAR"),
              //   onPressed: () {
               
              //   },
              // ),
              // ),
              RaisedButton(
                child: Text("ENVIAR"),
                onPressed: () {
                  this._addOrder();
                },
              ),
              // RaisedButton(
              //   child: Text("LISTA"),
              //   onPressed: () {
              //     _getOrders();
              //   },
              // ),
              // RaisedButton(
              //   child: Text("CANCELAR"),
              //   onPressed: () {
              //     _killOrder();
              //   },
              // ),
              // RaisedButton(
              //   child: Text("METHODS"),
              //   onPressed: () {
              //     _getMethods();
              //   },
              // ),
              Divider(),
              Text("$_status"),
            ],
          )),
    );
  }
}
