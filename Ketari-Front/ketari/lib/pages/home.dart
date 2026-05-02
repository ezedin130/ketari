import 'package:flutter/material.dart';
import 'package:get/get_core/src/get_main.dart';
import 'package:get/get_navigation/src/extension_navigation.dart';
import 'package:ketari/pages/profile.dart';

class Home extends StatefulWidget {
  const Home({super.key});

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size.height * 0.7;
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.lightGreenAccent,
        title: Text('Home'),
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
                      hintText: 'Search Jobs',
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
      body: SingleChildScrollView(
        child: Column(
          children: [
            SingleChildScrollView(
              child: SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: Padding(
                  padding: const EdgeInsets.only(top: 20.0, bottom: 10),
                  child: Row(
                    children: [
                      IconButton(
                        onPressed: () {},
                        icon: Icon(
                          Icons.filter_list,
                          color: Colors.lightGreenAccent,
                        ),
                      ),
                      TextButton(
                        onPressed: () {},
                        child: Text(
                          'On-site',
                          style: TextStyle(color: Colors.white),
                        ),
                      ),
                      TextButton(
                        onPressed: () {},
                        child: Text(
                          'Senior',
                          style: TextStyle(color: Colors.white),
                        ),
                      ),
                      TextButton(
                        onPressed: () {},
                        child: Text(
                          'Full-Time',
                          style: TextStyle(color: Colors.white),
                        ),
                      ),
                      TextButton(
                        onPressed: () {},
                        child: Text(
                          'Full-Time',
                          style: TextStyle(color: Colors.white),
                        ),
                      ),
                      TextButton(
                        onPressed: () {},
                        child: Text(
                          'Full-Time',
                          style: TextStyle(color: Colors.white),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: InkWell(
                onTap: () {
                  showModalBottomSheet(
                    context: context,
                    isScrollControlled: true,
                    backgroundColor: Color.fromARGB(255, 48, 47, 47),
                    builder: (context) {
                      return Container(
                        height: size,
                        child: Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: Column(
                            // crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Center(
                                child: Container(
                                  width: 40,
                                  height: 5,
                                  decoration: BoxDecoration(
                                    color: Colors.grey,
                                    borderRadius: BorderRadius.circular(10),
                                  ),
                                ),
                              ),
                              SizedBox(height: 20),
                              ListTile(
                                leading: Padding(
                                  padding: const EdgeInsets.all(8.0),
                                  child: Icon(Icons.person),
                                ),
                                title: Text(
                                  'Company Name',
                                  style: TextStyle(color: Colors.white),
                                ),
                                subtitle: Text(
                                  'time',
                                  style: TextStyle(color: Colors.grey),
                                ),
                              ),
                              Align(
                                alignment: Alignment.centerLeft,
                                child: Column(
                                  children: [
                                    Text(
                                      'Type of job',
                                      textAlign: TextAlign.left,
                                      style: TextStyle(color: Colors.white),
                                    ),
                                    Padding(
                                      padding: const EdgeInsets.all(8.0),
                                      child: Row(
                                        children: [
                                          SizedBox(width: 20),
                                          Icon(
                                            Icons.watch_later_outlined,
                                            color: Colors.white,
                                          ),
                                          SizedBox(width: 80),
                                          Icon(
                                            Icons.cases_outlined,
                                            color: Colors.white,
                                          ),
                                          SizedBox(width: 80),
                                          Icon(
                                            Icons.business_outlined,
                                            color: Colors.white,
                                          ),
                                          SizedBox(width: 70),
                                          Icon(
                                            Icons.location_on_outlined,
                                            color: Colors.white,
                                          ),
                                        ],
                                      ),
                                    ),
                                    Row(
                                      children: [
                                        Expanded(
                                          child: ListTile(
                                            title: Text(
                                              'Deadline',
                                              style: TextStyle(
                                                color: Colors.lightGreen,
                                              ),
                                            ),
                                            subtitle: Text(
                                              'date',
                                              style: TextStyle(
                                                color: Colors.white,
                                              ),
                                            ),
                                          ),
                                        ),
                                        Expanded(
                                          child: ListTile(
                                            title: Text(
                                              'Salary',
                                              style: TextStyle(
                                                color: Colors.lightGreen,
                                              ),
                                            ),
                                            subtitle: Text(
                                              'Amount of Money',
                                              style: TextStyle(
                                                color: Colors.white,
                                              ),
                                            ),
                                          ),
                                        ),
                                      ],
                                    ),
                                    Divider(thickness: 0.4),
                                    Text(
                                      'Job Description',
                                      style: TextStyle(color: Colors.white),
                                    ),
                                    Text(
                                      'ilnfcnnucffncuun',
                                      style: TextStyle(color: Colors.white),
                                    ),
                                    Divider(thickness: 0.4),
                                    Row(
                                      children: [
                                        Expanded(
                                          child: ListTile(
                                            leading: Icon(Icons.chair_outlined),
                                            title: Text(
                                              'Amount of Vacancy',
                                              style: TextStyle(
                                                color: Colors.lightGreen,
                                              ),
                                            ),
                                            subtitle: Text(
                                              'Number of Vacancy',
                                              style: TextStyle(
                                                color: Colors.white,
                                              ),
                                            ),
                                          ),
                                        ),
                                      ],
                                    ),
                                    Row(
                                      children: [
                                        Expanded(
                                          child: ListTile(
                                            leading: Icon(
                                              Icons.school_outlined,
                                            ),
                                            title: Text(
                                              'Education level',
                                              style: TextStyle(
                                                color: Colors.lightGreen,
                                              ),
                                            ),
                                            subtitle: Text(
                                              'Education level',
                                              style: TextStyle(
                                                color: Colors.white,
                                              ),
                                            ),
                                          ),
                                        ),
                                      ],
                                    ),
                                    Divider(thickness: 0.4),
                                    Text(
                                      'Skills',
                                      style: TextStyle(color: Colors.white),
                                    ),
                                  ],
                                ),
                              ),
                              SizedBox(height: 20),
                              ElevatedButton(
                                style: ButtonStyle(
                                  backgroundColor: WidgetStateProperty.all(
                                    Colors.lightGreen,
                                  ),
                                ),
                                onPressed: () {},
                                child: Text(
                                  'Apply',
                                  style: TextStyle(color: Colors.white),
                                ),
                              ),
                            ],
                          ),
                        ),
                      );
                    },
                  );
                },
                child: Card(
                  color: Colors.transparent,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                    side: BorderSide(
                      color: const Color.fromARGB(123, 255, 255, 255),
                    ),
                  ),
                  child: Column(
                    children: [
                      ListTile(
                        leading: Icon(Icons.person), //should be ima
                        title: Padding(
                          padding: const EdgeInsets.only(left: 10.0),
                          child: Text(
                            '2F capital',
                            style: TextStyle(color: Colors.white),
                          ),
                        ),
                        subtitle: Padding(
                          padding: const EdgeInsets.only(left: 10.0),
                          child: Text(
                            'Bole, Addis Abeba',
                            style: TextStyle(
                              color: const Color.fromARGB(118, 255, 255, 255),
                            ),
                          ),
                        ),
                      ),
                      Align(
                        alignment: Alignment.centerLeft,
                        child: Padding(
                          padding: const EdgeInsets.only(left: 10.0),
                          child: Text(
                            'Software Developer',
                            style: TextStyle(color: Colors.white),
                          ),
                        ),
                      ),
                      Align(
                        alignment: Alignment.centerLeft,
                        child: Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: Text(
                            '25000 - 30000 (ETB)',
                            style: TextStyle(color: Colors.lightGreenAccent),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
      backgroundColor: Color.fromARGB(255, 48, 47, 47),
    );
  }
}
