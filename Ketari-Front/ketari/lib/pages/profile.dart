import 'package:flutter/material.dart';

class Profile extends StatelessWidget {
  const Profile({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.lightGreenAccent,
        title: Padding(
          padding: const EdgeInsets.only(top: 10),
          child: Text('Profile'),
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
                ),
              ),
            ),
          ),
        ),
      ),
      backgroundColor: Color.fromARGB(255, 48, 47, 47),
    );
  }
}
