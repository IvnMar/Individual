import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  static const String baseUrl = 'http://10.0.2.2:8080/api'; // Cambia la IP si usas dispositivo físico

  static Future<String?> login(String username, String password) async {
    final url = Uri.parse('$baseUrl/auth/login');
    final response = await http.post(
      url,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'username': username, 'password': password}),
    );
    if (response.statusCode == 200) {
      // Suponiendo que el backend responde con un token
      final data = jsonDecode(response.body);
      return data['token'];
    }
    return null;
  }

  static Future<String?> register(String username, String password) async {
    final url = Uri.parse('$baseUrl/auth/register');
    final response = await http.post(
      url,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'username': username, 'password': password}),
    );
    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return data['token'];
    }
    return null;
  }

  static Future<List<dynamic>?> getRecomendaciones(String token) async {
    final url = Uri.parse('$baseUrl/recomendaciones');
    final response = await http.get(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );
    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    }
    return null;
  }

  static Future<List<dynamic>?> buscarPeliculas(String query, String token) async {
    final url = Uri.parse('$baseUrl/peliculas/buscar?titulo=$query');
    final response = await http.get(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );
    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    }
    return null;
  }

  static Future<List<dynamic>?> buscarLibros(String query, String token) async {
    final url = Uri.parse('$baseUrl/libros/buscar?titulo=$query');
    final response = await http.get(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );
    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    }
    return null;
  }

  static Future<List<dynamic>?> listarFavoritos(String tipo, String token) async {
    final url = Uri.parse('$baseUrl/favoritos/listar?tipo=$tipo');
    final response = await http.get(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );
    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    }
    return null;
  }

  static Future<bool> agregarFavorito(Map<String, dynamic> favorito, String token) async {
    final url = Uri.parse('$baseUrl/favoritos/agregar');
    final response = await http.post(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
      body: jsonEncode(favorito),
    );
    return response.statusCode == 200;
  }

  static Future<bool> eliminarFavorito(String tipo, String contenidoId, String token) async {
    final url = Uri.parse('$baseUrl/favoritos/eliminar?tipo=$tipo&contenidoId=$contenidoId');
    final response = await http.delete(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );
    return response.statusCode == 200;
  }
}