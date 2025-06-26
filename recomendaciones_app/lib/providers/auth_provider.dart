import 'package:flutter/material.dart';
import '../services/api_service.dart';

class AuthProvider with ChangeNotifier {
  bool _isAuthenticated = false;
  String? _token;

  bool get isAuthenticated => _isAuthenticated;
  String? get token => _token;

  Future<bool> login(String username, String password) async {
    final result = await ApiService.login(username, password);
    if (result != null) {
      _token = result;
      _isAuthenticated = true;
      notifyListeners();
      return true;
    }
    return false;
  }

  Future<bool> register(String username, String password) async {
    final result = await ApiService.register(username, password);
    if (result != null) {
      _token = result;
      _isAuthenticated = true;
      notifyListeners();
      return true;
    }
    return false;
  }

  void logout() {
    _token = null;
    _isAuthenticated = false;
    notifyListeners();
  }
}