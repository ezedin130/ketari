import 'package:flutter/material.dart';

class Notify extends StatelessWidget {
  const Notify({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.lightGreenAccent,
        title: Padding(
          padding: const EdgeInsets.only(top: 10),
          child: Text('Messages'),
        ),
        automaticallyImplyLeading: false,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadiusGeometry.vertical(
            bottom: Radius.elliptical(90, 40),
          ),
        ),
        bottom: PreferredSize(
          preferredSize: Size.fromHeight(75),
          child: Transform.translate(
            offset: Offset(0, 30),
            child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: Padding(
                padding: const EdgeInsets.all(15.0),
                child: Material(
                  elevation: 8,
                  borderRadius: BorderRadius.circular(16),
                  child: TextField(
                    decoration: InputDecoration(
                      hintStyle: TextStyle(color: Colors.white),
                      hintText: 'Search Messages',
                      prefixIcon: Icon(Icons.search, color: Colors.white),
                      filled: true,
                      fillColor: const Color.fromARGB(255, 55, 55, 55),
                      contentPadding: EdgeInsets.symmetric(vertical: 14),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(16),
                        borderSide: BorderSide.none,
                      ),
                    ),
                    style: TextStyle(color: Colors.white),
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
      body: Center(
        child: Text('No Messages Yet', style: TextStyle(color: Colors.white)),
      ),
      backgroundColor: Color.fromARGB(255, 48, 47, 47),
    );
  }
}
