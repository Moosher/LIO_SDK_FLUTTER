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
  String _status = "Nope";
  final control = TextEditingController();
  int _orders = 0;
  static const platform =
      const MethodChannel('com.example.primeiro_projeto/service');

  void _addOrder() {
    try {
      platform.invokeMethod(
          "start", <String, String>{"valor": control.text, "param2": "test2"});
    } catch (e) {
      print(e);
    }
  }

  void _destroyBind() {
    try {
      platform.invokeMethod("destroy");
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

  void _getOrder() {
    try {
      platform.invokeMethod("order");
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
                controller: control,
                decoration: InputDecoration(labelText: "Valor"),
              ),
              Divider(),
              RaisedButton(
                child: Text("ADICIONAR"),
                onPressed: () {
                  this._addOrder();
                },
              ),
              RaisedButton(
                child: Text("ENVIAR"),
                onPressed: () {
                  this._payOrder();
                },
              ),
              RaisedButton(
                child: Text("LISTA"),
                onPressed: () {
                  _getOrder();
                },
              ),
              RaisedButton(
                child: Text("METHODS"),
                onPressed: () {
                  _getMethods();
                },
              ),
              Divider(),
              Text("Status: $_status"),
            ],
          )),
    );
  }
}
