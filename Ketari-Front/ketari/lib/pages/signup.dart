import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:ketari/controllers/authController.dart';
import 'package:ketari/main_screen.dart';
import 'package:ketari/pages/login.dart';

class Signup extends StatelessWidget {
  const Signup({super.key});

  @override
  Widget build(BuildContext context) {
    final firstNameController = TextEditingController();
    final emailController = TextEditingController();
    final lastNameController = TextEditingController();
    final size = MediaQuery.of(context).size;
    return Scaffold(
      appBar: AppBar(
        automaticallyImplyLeading: false,
        backgroundColor: Colors.white,
        title: Padding(
          padding: const EdgeInsets.only(top: 20),
          child: Image.asset(
            'assets/images/ketari.png',
            height: size.height * 0.1,
            width: size.width * 1,
          ),
        ),
        bottom: PreferredSize(
          preferredSize: Size.fromHeight(60),
          child: SizedBox(height: size.height * 0.05),
        ),
      ),
      body: SingleChildScrollView(
        child: Container(
          height: MediaQuery.of(context).size.height,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(20),
            color: const Color.fromARGB(255, 48, 47, 47),
          ),
          child: Padding(
            padding: const EdgeInsets.all(20.0),
            child: Column(
              children: [
                Text(
                  ' Signup ',
                  textAlign: TextAlign.center,
                  style: GoogleFonts.lato(
                    fontSize: size.width * 0.07,
                    fontWeight: FontWeight.w600,
                    color: Colors.white,
                  ),
                ),
                SizedBox(height: size.height * 0.05),
                Row(
                  children: [
                    Expanded(
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: ReusableTextField(
                          controller: firstNameController,
                          hint: "First Name",
                          keyboardtype: TextInputType.phone,
                        ),
                      ),
                    ),
                    Expanded(
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: ReusableTextField(
                          controller: lastNameController,
                          hint: "Last Name",
                          keyboardtype: TextInputType.text,
                        ),
                      ),
                    ),
                  ],
                ),
                SizedBox(height: size.height * 0.03),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: ReusableTextField(
                    controller: emailController,
                    hint: "Email",
                    keyboardtype: TextInputType.text,
                  ),
                ),
                SizedBox(height: size.height * 0.03),
                ElevatedButton(
                  onPressed: () async {
                    final authController = Authcontroller();
                    final result = await authController.registerUser(
                      firstNameController.text,
                      emailController.text,
                      lastNameController.text,
                    );
                    ScaffoldMessenger.of(
                      context,
                    ).showSnackBar(SnackBar(content: Text(result.message)));
                    if (result.success) {
                      Get.to(MainScreen());
                    }
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.lightGreenAccent,
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        "Sign Up",
                        style: GoogleFonts.inter(
                          fontSize: 16,
                          fontWeight: FontWeight.w400,
                          color: const Color.fromARGB(255, 61, 59, 59),
                        ),
                      ),
                    ],
                  ),
                ),
                SizedBox(height: size.height * 0.04),
                Row(
                  children: [
                    Expanded(child: Divider(color: Colors.black)),
                    Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Text(
                        'Or continue with',
                        style: TextStyle(color: Colors.white),
                      ),
                    ),
                    Expanded(child: Divider(color: Colors.black)),
                  ],
                ),
                SizedBox(height: size.height * 0.03),
                ElevatedButton(
                  onPressed: () {},
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.lightGreenAccent,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadiusGeometry.circular(20),
                    ),
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Image.asset(
                        'assets/images/google.png',
                        height: 24,
                        width: 24,
                      ),
                      SizedBox(width: size.width * 0.02),
                      Text("Google"),
                    ],
                  ),
                ),
                SizedBox(height: size.height * 0.02),
                ElevatedButton(
                  onPressed: () {},
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.lightGreenAccent,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadiusGeometry.circular(20),
                    ),
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Image.asset(
                        'assets/images/linkedin.png',
                        height: 24,
                        width: 24,
                      ),
                      SizedBox(width: size.width * 0.02),
                      Text("Linkden"),
                    ],
                  ),
                ),
                SizedBox(height: size.height * 0.07),
                Row(
                  children: [
                    Padding(
                      padding: const EdgeInsets.only(left: 67),
                      child: Text(
                        'Already have an account?',
                        style: TextStyle(color: Colors.white),
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.only(left: 0.1),
                      child: TextButton(
                        onPressed: () async {
                          await Get.to(() => Login());
                        },
                        child: Text(
                          'Login',
                          style: TextStyle(color: Colors.lightGreenAccent),
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class ReusableTextField extends StatelessWidget {
  const ReusableTextField({
    super.key,
    required this.controller,
    required this.hint,
    required this.keyboardtype,
  });

  final TextEditingController controller;
  final String hint;
  final TextInputType keyboardtype;

  @override
  Widget build(BuildContext context) {
    return TextFormField(
      controller: controller,
      decoration: InputDecoration(
        border: OutlineInputBorder(),
        enabledBorder: OutlineInputBorder(
          borderSide: BorderSide(color: Colors.white),
          borderRadius: BorderRadius.circular(10),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
          borderSide: BorderSide(color: Colors.lightGreenAccent),
        ),
        hintStyle: TextStyle(color: Colors.white),
        hintText: hint,
      ),
      keyboardType: keyboardtype,
      style: TextStyle(color: Colors.white),
    );
  }
}
