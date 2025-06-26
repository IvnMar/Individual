import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth_provider.dart';
import '../services/api_service.dart';
import 'search_screen.dart';
import 'favoritos_screen.dart';


class HomeScreen extends StatefulWidget {
  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  List<dynamic>? recomendaciones;
  bool loading = true;

  @override
  void initState() {
    super.initState();
    cargarRecomendaciones();
  }

  Future<void> cargarRecomendaciones() async {
    final token = Provider.of<AuthProvider>(context, listen: false).token;
    final data = await ApiService.getRecomendaciones(token!);
    setState(() {
      recomendaciones = data;
      loading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);

    return Scaffold(
      appBar: AppBar(
        title: Text('Inicio'),
        actions: [
          IconButton(
            icon: Icon(Icons.logout),
            onPressed: () {
              authProvider.logout();
            },
          ),
        ],
      ),
      body: loading
          ? Center(child: CircularProgressIndicator())
          : Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    ElevatedButton(
                      child: Text('Buscar Películas'),
                      onPressed: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (_) => SearchScreen(isPelicula: true),
                          ),
                        );
                      },
                    ),
                    SizedBox(width: 10),
                    ElevatedButton(
                      child: Text('Buscar Libros'),
                      onPressed: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (_) => SearchScreen(isPelicula: false),
                          ),
                        );
                      },
                    ),
                  ],
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    ElevatedButton(
                      child: Text('Favoritos Películas'),
                      onPressed: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (_) => FavoritosScreen(tipo: "PELICULA"),
                          ),
                        );
                      },
                    ),
                    SizedBox(width: 10),
                    ElevatedButton(
                      child: Text('Favoritos Libros'),
                      onPressed: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (_) => FavoritosScreen(tipo: "LIBRO"),
                          ),
                        );
                      },
                    ),
                  ],
                ),
                Expanded(
                  child: recomendaciones == null || recomendaciones!.isEmpty
                      ? Center(child: Text('No hay recomendaciones.'))
                      : ListView.builder(
                          itemCount: recomendaciones!.length,
                          itemBuilder: (context, index) {
                            final rec = recomendaciones![index];
                            return ListTile(
                              title: Text(rec['titulo'] ?? 'Sin título'),
                              subtitle: Text(rec['descripcion'] ?? ''),
                            );
                          },
                        ),
                ),
              ],
            ),
    );
  }
}