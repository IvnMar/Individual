import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth_provider.dart';
import '../services/api_service.dart';

class SearchScreen extends StatefulWidget {
  final bool isPelicula; // true para películas, false para libros
  const SearchScreen({Key? key, required this.isPelicula}) : super(key: key);

  @override
  State<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> {
  final _controller = TextEditingController();
  List<dynamic>? resultados;
  bool loading = false;

  void buscar() async {
    setState(() {
      loading = true;
      resultados = null;
    });
    final token = Provider.of<AuthProvider>(context, listen: false).token!;
    if (widget.isPelicula) {
      resultados = await ApiService.buscarPeliculas(_controller.text, token);
    } else {
      resultados = await ApiService.buscarLibros(_controller.text, token);
    }
    setState(() {
      loading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.isPelicula ? 'Buscar Películas' : 'Buscar Libros'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            TextField(
              controller: _controller,
              decoration: InputDecoration(
                labelText: widget.isPelicula ? 'Título de película' : 'Título de libro',
                suffixIcon: IconButton(
                  icon: Icon(Icons.search),
                  onPressed: buscar,
                ),
              ),
              onSubmitted: (_) => buscar(),
            ),
            SizedBox(height: 20),
            if (loading) CircularProgressIndicator(),
            if (resultados != null)
              Expanded(
                child: ListView.builder(
                  itemCount: resultados!.length,
                  itemBuilder: (context, index) {
                    final item = resultados![index];
                    return ListTile(
                      title: Text(item['titulo'] ?? 'Sin título'),
                      subtitle: Text(item['descripcion'] ?? ''),
                      trailing: IconButton(
                        icon: Icon(Icons.favorite_border),
                        onPressed: () async {
                          final token = Provider.of<AuthProvider>(context, listen: false).token!;
                          final favorito = {
                            "tipo": widget.isPelicula ? "PELICULA" : "LIBRO",
                            "contenidoId": item['contenidoId'] ?? item['id'] ?? '',
                            "titulo": item['titulo'] ?? '',
                            "autorDirector": item['autorDirector'] ?? item['autor'] ?? '',
                            "imagenUrl": item['imagenUrl'] ?? '',
                            "anioPublicacion": item['anioPublicacion'] ?? '',
                            "descripcion": item['descripcion'] ?? '',
                            // Puedes agregar más campos si tu backend los requiere
                          };
                          final ok = await ApiService.agregarFavorito(favorito, token);
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(content: Text(ok ? 'Agregado a favoritos' : 'Error al agregar')),
                          );
                        },
                      ),
                    );
                  },
                ),
              ),
          ],
        ),
      ),
    );
  }
}