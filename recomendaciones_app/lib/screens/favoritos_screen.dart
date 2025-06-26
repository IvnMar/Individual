import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth_provider.dart';
import '../services/api_service.dart';

class FavoritosScreen extends StatefulWidget {
  final String tipo; // "PELICULA" o "LIBRO"
  const FavoritosScreen({Key? key, required this.tipo}) : super(key: key);

  @override
  State<FavoritosScreen> createState() => _FavoritosScreenState();
}

class _FavoritosScreenState extends State<FavoritosScreen> {
  List<dynamic>? favoritos;
  bool loading = true;

  @override
  void initState() {
    super.initState();
    cargarFavoritos();
  }

  Future<void> cargarFavoritos() async {
    final token = Provider.of<AuthProvider>(context, listen: false).token!;
    favoritos = await ApiService.listarFavoritos(widget.tipo, token);
    setState(() {
      loading = false;
    });
  }

  Future<void> eliminar(String contenidoId) async {
    final token = Provider.of<AuthProvider>(context, listen: false).token!;
    final ok = await ApiService.eliminarFavorito(widget.tipo, contenidoId, token);
    if (ok) {
      cargarFavoritos();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Favoritos de ${widget.tipo == "PELICULA" ? "Películas" : "Libros"}'),
      ),
      body: loading
          ? Center(child: CircularProgressIndicator())
          : favoritos == null || favoritos!.isEmpty
              ? Center(child: Text('No hay favoritos.'))
              : ListView.builder(
                  itemCount: favoritos!.length,
                  itemBuilder: (context, index) {
                    final fav = favoritos![index];
                    return ListTile(
                      title: Text(fav['titulo'] ?? 'Sin título'),
                      subtitle: Text(fav['descripcion'] ?? ''),
                      trailing: IconButton(
                        icon: Icon(Icons.delete),
                        onPressed: () => eliminar(fav['contenidoId']),
                      ),
                    );
                  },
                ),
    );
  }
}