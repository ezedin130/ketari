class AuthResponse {
  final bool success;
  final String message;
  final String? token;

  AuthResponse({
    required this.success,
    required this.message,
    this.token,
  });

  factory AuthResponse.success(Map<String, dynamic> json) {
    return AuthResponse(
      success: true,
      message: json['message'] ?? '',
      token: json['token'],
    );
  }

  factory AuthResponse.error(Map<String, dynamic> json) {
    return AuthResponse(
      success: false,
      message: json['error'] ?? 'Something went wrong',
    );
  }
}
