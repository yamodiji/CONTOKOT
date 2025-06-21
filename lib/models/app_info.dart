import 'dart:typed_data';
import 'package:installed_apps/app_info.dart' as installed_apps;

class AppInfo {
  final String appName;
  final String packageName;
  final String? systemAppName;
  final String? versionName;
  final int? versionCode;
  final String? dataDir;
  final bool systemApp;
  final int installTimeMillis;
  final int updateTimeMillis;
  final Uint8List? icon;
  final String category;
  final bool enabled;
  
  // Performance tracking
  int launchCount;
  int lastLaunchTime;
  bool isFavorite;
  double searchScore;

  AppInfo({
    required this.appName,
    required this.packageName,
    this.systemAppName,
    this.versionName,
    this.versionCode,
    this.dataDir,
    this.systemApp = false,
    this.installTimeMillis = 0,
    this.updateTimeMillis = 0,
    this.icon,
    this.category = 'Unknown',
    this.enabled = true,
    this.launchCount = 0,
    this.lastLaunchTime = 0,
    this.isFavorite = false,
    this.searchScore = 0.0,
  });

  factory AppInfo.fromInstalledApp(installed_apps.AppInfo app) {
    return AppInfo(
      appName: app.name,
      packageName: app.packageName,
      systemAppName: app.name, // installed_apps doesn't distinguish system app name
      versionName: app.versionName,
      versionCode: app.versionCode,
      dataDir: null, // not available in installed_apps
      systemApp: false, // will be determined separately
      installTimeMillis: app.installedTimestamp,
      updateTimeMillis: app.installedTimestamp,
      icon: app.icon,
      category: 'Unknown', // not available in installed_apps
      enabled: true, // assume enabled if it's returned by the API
    );
  }

  // Create a copy with updated values
  AppInfo copyWith({
    String? appName,
    String? packageName,
    String? systemAppName,
    String? versionName,
    int? versionCode,
    String? dataDir,
    bool? systemApp,
    int? installTimeMillis,
    int? updateTimeMillis,
    Uint8List? icon,
    String? category,
    bool? enabled,
    int? launchCount,
    int? lastLaunchTime,
    bool? isFavorite,
    double? searchScore,
  }) {
    return AppInfo(
      appName: appName ?? this.appName,
      packageName: packageName ?? this.packageName,
      systemAppName: systemAppName ?? this.systemAppName,
      versionName: versionName ?? this.versionName,
      versionCode: versionCode ?? this.versionCode,
      dataDir: dataDir ?? this.dataDir,
      systemApp: systemApp ?? this.systemApp,
      installTimeMillis: installTimeMillis ?? this.installTimeMillis,
      updateTimeMillis: updateTimeMillis ?? this.updateTimeMillis,
      icon: icon ?? this.icon,
      category: category ?? this.category,
      enabled: enabled ?? this.enabled,
      launchCount: launchCount ?? this.launchCount,
      lastLaunchTime: lastLaunchTime ?? this.lastLaunchTime,
      isFavorite: isFavorite ?? this.isFavorite,
      searchScore: searchScore ?? this.searchScore,
    );
  }

  // Convert to JSON for storage
  Map<String, dynamic> toJson() {
    return {
      'appName': appName,
      'packageName': packageName,
      'systemAppName': systemAppName,
      'versionName': versionName,
      'versionCode': versionCode,
      'dataDir': dataDir,
      'systemApp': systemApp,
      'installTimeMillis': installTimeMillis,
      'updateTimeMillis': updateTimeMillis,
      'category': category,
      'enabled': enabled,
      'launchCount': launchCount,
      'lastLaunchTime': lastLaunchTime,
      'isFavorite': isFavorite,
      'searchScore': searchScore,
    };
  }

  // Create from JSON
  factory AppInfo.fromJson(Map<String, dynamic> json) {
    return AppInfo(
      appName: json['appName'] ?? '',
      packageName: json['packageName'] ?? '',
      systemAppName: json['systemAppName'],
      versionName: json['versionName'],
      versionCode: json['versionCode'],
      dataDir: json['dataDir'],
      systemApp: json['systemApp'] ?? false,
      installTimeMillis: json['installTimeMillis'] ?? 0,
      updateTimeMillis: json['updateTimeMillis'] ?? 0,
      category: json['category'] ?? 'Unknown',
      enabled: json['enabled'] ?? true,
      launchCount: json['launchCount'] ?? 0,
      lastLaunchTime: json['lastLaunchTime'] ?? 0,
      isFavorite: json['isFavorite'] ?? false,
      searchScore: json['searchScore']?.toDouble() ?? 0.0,
    );
  }

  // Check if app matches search query
  bool matchesQuery(String query) {
    if (query.isEmpty) return true;
    
    final lowerQuery = query.toLowerCase();
    final lowerAppName = appName.toLowerCase();
    final lowerPackageName = packageName.toLowerCase();
    
    return lowerAppName.contains(lowerQuery) ||
           lowerPackageName.contains(lowerQuery) ||
           (systemAppName?.toLowerCase().contains(lowerQuery) ?? false);
  }

  // Get display name (prefer app name over system name)
  String get displayName => appName.isNotEmpty ? appName : (systemAppName ?? packageName);

  // Check if this is a launcher app
  bool get isLauncher => packageName.contains('launcher') || 
                        category.toLowerCase().contains('launcher');

  // Check if this is a system app that should be hidden
  bool get shouldHide => systemApp && 
                        (packageName.startsWith('com.android.') ||
                         packageName.startsWith('com.google.android.') ||
                         packageName.contains('packageinstaller') ||
                         packageName.contains('wallpaper'));

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is AppInfo &&
          runtimeType == other.runtimeType &&
          packageName == other.packageName;

  @override
  int get hashCode => packageName.hashCode;

  @override
  String toString() => 'AppInfo(name: $appName, package: $packageName)';
} 