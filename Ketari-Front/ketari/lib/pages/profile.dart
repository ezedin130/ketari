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
                padding: const EdgeInsets.only(top: 20.0, left: 10, right: 10),
                child: Material(
                  elevation: 8,
                  borderRadius: BorderRadius.circular(16),
                  child: ListTile(
                    tileColor: Color.fromARGB(255, 48, 47, 47),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadiusGeometry.all(
                        Radius.circular(10),
                      ),
                    ),
                    leading: CircleAvatar(
                      radius: 30,
                      backgroundColor: Colors.lightGreenAccent,
                      child: Icon(Icons.person),
                    ),
                    title: Text(
                      'Full Name',
                      style: TextStyle(color: Colors.white),
                    ),
                    subtitle: Text(
                      'Phone Number',
                      style: TextStyle(color: Colors.white),
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
      body: Padding(
        padding: const EdgeInsets.only(top: 40.0, left: 20, right: 20),
        child: Column(
          children: [
            ListTile(
              leading: Icon(Icons.person, color: Colors.white),
              title: Text(
                'Edit Profile',
                style: TextStyle(color: Colors.white),
              ),
            ),
            Divider(),
            ListTile(
              leading: Icon(Icons.settings, color: Colors.white),
              title: Text(
                'Settings and Privacy',
                style: TextStyle(color: Colors.white),
              ),
            ),
            Divider(),
            ListTile(
              leading: Icon(Icons.question_mark, color: Colors.white),
              title: Text(
                'Help and support',
                style: TextStyle(color: Colors.white),
              ),
            ),
            Divider(),
          ],
        ),
      ),
      backgroundColor: Color.fromARGB(255, 48, 47, 47),
    );
  }
}
