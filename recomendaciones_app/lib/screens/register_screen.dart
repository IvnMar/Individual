import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth_provider.dart';

class RegisterScreen extends StatefulWidget {
  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();
  String _username = '';
  String _password = '';
  bool _loading = false;
  String? _error;

  void _register(BuildContext context) async {
    if (!_formKey.currentState!.validate()) return;
    setState(() {
      _loading = true;
      _error = null;
    });
    _formKey.currentState!.save();
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    final success = await authProvider.register(_username, _password);
    setState(() {
      _loading = false;
      if (!success) {
        _error = 'No se pudo registrar. Intenta con otro usuario.';
      } else {
        Navigator.pop(context); // Regresa al login si el registro fue exitoso
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Registro')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              TextFormField(
                decoration: InputDecoration(labelText: 'Usuario'),
                validator: (value) =>
                    value == null || value.isEmpty ? 'Campo requerido' : null,
                onSaved: (value) => _username = value!,
              ),
              TextFormField(
                decoration: InputDecoration(labelText: 'Contraseña'),
                obscureText: true,
                validator: (value) =>
                    value == null || value.isEmpty ? 'Campo requerido' : null,
                onSaved: (value) => _password = value!,
              ),
              SizedBox(height: 20),
              if (_loading) CircularProgressIndicator(),
              if (_error != null)
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Text(_error!, style: TextStyle(color: Colors.red)),
                ),
              ElevatedButton(
                onPressed: _loading ? null : () => _register(context),
                child: Text('Registrarse'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}