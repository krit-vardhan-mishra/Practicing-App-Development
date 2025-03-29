import 'package:flutter/material.dart';
import 'package:tw_clone/main.dart';

class Signup extends StatefulWidget {
  const Signup({super.key});

  @override
  State<Signup> createState() => _SignupState();
}

class _SignupState extends State<Signup> {
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final GlobalKey<FormState> _formKey = GlobalKey();
  final RegExp _emailRegExp = RegExp(
    r"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$",
  );

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Form(
        key: _formKey,
        autovalidateMode: AutovalidateMode.onUserInteraction,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text(
              "Sign Up for Twitter",
              style: TextStyle(fontSize: 25.0, fontWeight: FontWeight.bold),
            ),
            Container(
              margin: const EdgeInsets.fromLTRB(15, 30, 15, 0),
              decoration: BoxDecoration(
                color: Colors.grey.shade200,
                borderRadius: BorderRadius.circular(30),
              ),
              child: TextFormField(
                controller: _emailController,
                keyboardType: TextInputType.emailAddress,
                decoration: const InputDecoration(
                  border: InputBorder.none,
                  hintText: "Enter Your Email",
                  contentPadding: EdgeInsets.symmetric(
                    vertical: 15,
                    horizontal: 20,
                  ),
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return "Please Enter Your Email";
                  } else if (!_emailRegExp.hasMatch(value)) {
                    return "Please Enter a Valid Email";
                  }
                  return null;
                },
              ),
            ),
            Container(
              margin: const EdgeInsets.all(15),
              decoration: BoxDecoration(
                color: Colors.grey.shade200,
                borderRadius: BorderRadius.circular(30),
              ),
              child: TextFormField(
                controller: _passwordController,
                obscureText: true,
                decoration: const InputDecoration(
                  border: InputBorder.none,
                  hintText: "Enter Your Password",
                  contentPadding: EdgeInsets.symmetric(
                    vertical: 15,
                    horizontal: 20,
                  ),
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return "Please Enter Your Password";
                  } else if (value.length < 8) {
                    return "Password must be at least 8 characters";
                  }
                  return null;
                },
              ),
            ),
            Container(
              width: 250,
              decoration: BoxDecoration(
                color: Colors.blue, borderRadius: BorderRadius.circular(30),
              ),
              child: TextButton(
                onPressed: () {
                  if (_formKey.currentState!.validate()) {
                    debugPrint(_emailController.text);
                    debugPrint(_passwordController.text);
                  } else {
                    debugPrint("Form is not valid");
                  }
                },
                child: const Text("Login", style: TextStyle(color: Colors.white)),
              ),
            ),
            TextButton(
              onPressed: () async {
                await Navigator.of(context).push(
                  MaterialPageRoute(builder: (context) => const Signup()),
                );
              },
              child: const Text(
                "Don't have an account? Sign up here",
                style: TextStyle(color: Colors.blue),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
