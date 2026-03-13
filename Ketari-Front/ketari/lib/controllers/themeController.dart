import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get/state_manager.dart';

class ThemeController extends GetxController {
  RxBool isDark = true.obs;

  void toggleTheme() {
    isDark.value = !isDark.value;
  }
}
