import 'dart:async';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:installed_apps/installed_apps.dart';
import 'package:installed_apps/app_info.dart' as installed_apps;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:fuzzy/fuzzy.dart';

import '../models/app_info.dart';
import '../utils/constants.dart';

class AppProvider extends ChangeNotifier {
  List<AppInfo> _allApps = [];
  List<AppInfo> _filteredApps = [];
  List<AppInfo> _favoriteApps = [];
  List<AppInfo> _mostUsedApps = [];
  List<String> _searchHistory = [];
  
  String _searchQuery = '';
  bool _isLoading = false;
  bool _fuzzySearchEnabled = true;
  bool _showMostUsed = true;
  bool _autoFocus = true;
  
  Timer? _debounceTimer;
  late Fuzzy<AppInfo> _fuzzySearcher;
  SharedPreferences? _prefs;

  // Getters
  List<AppInfo> get allApps => _allApps;
  List<AppInfo> get filteredApps => _filteredApps;
  List<AppInfo> get favoriteApps => _favoriteApps;
  List<AppInfo> get mostUsedApps => _mostUsedApps;
  List<String> get searchHistory => _searchHistory;
  String get searchQuery => _searchQuery;
  bool get isLoading => _isLoading;
  bool get fuzzySearchEnabled => _fuzzySearchEnabled;
  bool get showMostUsed => _showMostUsed;
  bool get autoFocus => _autoFocus;

  AppProvider() {
    _initializeFuzzySearcher();
    _loadApps();
  }

  void _initializeFuzzySearcher() {
    _fuzzySearcher = Fuzzy<AppInfo>(
      _allApps,
      options: FuzzyOptions(
        keys: [
          WeightedKey(
            name: 'appName',
            getter: (app) => app.appName,
            weight: 1.0,
          ),
          WeightedKey(
            name: 'packageName',
            getter: (app) => app.packageName,
            weight: 0.5,
          ),
          WeightedKey(
            name: 'systemAppName',
            getter: (app) => app.systemAppName ?? '',
            weight: 0.8,
          ),
        ],
        threshold: AppConstants.searchThreshold,
        shouldSort: true,
      ),
    );
  }

  Future<void> _loadApps() async {
    _setLoading(true);
    
    try {
      // Load preferences
      _prefs = await SharedPreferences.getInstance();
      await _loadSettings();
      
      // Get installed apps
      final installedApps = await InstalledApps.getInstalledApps(
        true, // exclude system apps
        true, // include icons
        '', // no package name prefix filter
      );

      // Convert to AppInfo objects and filter out unwanted apps
      _allApps = [];
      for (var app in installedApps) {
        // Check if it's a system app separately
        final isSystemApp = await InstalledApps.isSystemApp(app.packageName);
        
        if (isSystemApp != true && !_shouldHideApp(app)) {
          final appInfo = AppInfo.fromInstalledApp(app);
          _allApps.add(appInfo.copyWith(systemApp: isSystemApp == true));
        }
      }

      // Load stored app data (launch counts, favorites, etc.)
      await _loadStoredAppData();
      
      // Sort apps by usage and name
      _sortApps();
      
      // Initialize filtered apps
      _filteredApps = List.from(_allApps);
      
      // Update fuzzy searcher
      _initializeFuzzySearcher();
      
      // Load favorites and most used
      _updateFavoriteApps();
      _updateMostUsedApps();
      
    } catch (e) {
      debugPrint('Error loading apps: $e');
    } finally {
      _setLoading(false);
    }
  }

  bool _shouldHideApp(installed_apps.AppInfo app) {
    // Hide certain packages
    final hidePackages = [
      'com.android.launcher',
      'com.google.android.launcher',
      'com.sec.android.app.launcher',
      'com.miui.home',
      'com.oneplus.launcher',
      'com.android.settings',
      'com.android.packageinstaller',
    ];
    
    return hidePackages.any((pkg) => app.packageName.contains(pkg));
  }

  void _setLoading(bool loading) {
    _isLoading = loading;
    notifyListeners();
  }

  void _sortApps() {
    _allApps.sort((a, b) {
      // First sort by favorites
      if (a.isFavorite && !b.isFavorite) return -1;
      if (!a.isFavorite && b.isFavorite) return 1;
      
      // Then by launch count
      final countComparison = b.launchCount.compareTo(a.launchCount);
      if (countComparison != 0) return countComparison;
      
      // Finally by name
      return a.displayName.toLowerCase().compareTo(b.displayName.toLowerCase());
    });
  }

  Future<void> _loadStoredAppData() async {
    if (_prefs == null) return;
    
    // Load favorites
    final favoritePackages = _prefs!.getStringList(AppConstants.favoriteAppsKey) ?? [];
    
    // Load most used apps data
    final mostUsedData = _prefs!.getString(AppConstants.mostUsedAppsKey);
    Map<String, dynamic> usageData = {};
    if (mostUsedData != null) {
      try {
        usageData = jsonDecode(mostUsedData);
      } catch (e) {
        debugPrint('Error parsing usage data: $e');
      }
    }
    
    // Update app data
    for (var app in _allApps) {
      app.isFavorite = favoritePackages.contains(app.packageName);
      
      if (usageData.containsKey(app.packageName)) {
        final data = usageData[app.packageName];
        if (data != null) {
          app.launchCount = data['launchCount'] ?? 0;
          app.lastLaunchTime = data['lastLaunchTime'] ?? 0;
        }
      }
    }
  }

  Future<void> _saveAppData() async {
    if (_prefs == null) return;
    
    // Save favorites
    final favoritePackages = _allApps
        .where((app) => app.isFavorite)
        .map((app) => app.packageName)
        .toList();
    await _prefs!.setStringList(AppConstants.favoriteAppsKey, favoritePackages);
    
    // Save usage data
    final usageData = <String, dynamic>{};
    for (var app in _allApps) {
      if (app.launchCount > 0) {
        usageData[app.packageName] = {
          'launchCount': app.launchCount,
          'lastLaunchTime': app.lastLaunchTime,
        };
      }
    }
    await _prefs!.setString(AppConstants.mostUsedAppsKey, jsonEncode(usageData));
  }

  Future<void> _loadSettings() async {
    if (_prefs == null) return;
    
    _fuzzySearchEnabled = _prefs!.getBool(AppConstants.fuzzySearchKey) ?? true;
    _showMostUsed = _prefs!.getBool(AppConstants.showMostUsedKey) ?? true;
    _autoFocus = _prefs!.getBool(AppConstants.autoFocusKey) ?? true;
    _searchHistory = _prefs!.getStringList(AppConstants.searchHistoryKey) ?? [];
  }

  void search(String query) {
    _searchQuery = query.trim();
    
    // Debounce search for performance
    _debounceTimer?.cancel();
    _debounceTimer = Timer(
      Duration(milliseconds: AppConstants.debounceDelayMs),
      () => _performSearch(),
    );
  }

  void _performSearch() {
    if (_searchQuery.isEmpty) {
      _filteredApps = List.from(_allApps);
    } else {
      if (_fuzzySearchEnabled && _searchQuery.length >= AppConstants.minSearchLength) {
        // Use fuzzy search
        final results = _fuzzySearcher.search(_searchQuery);
        _filteredApps = results
            .take(AppConstants.maxSearchResults)
            .map((result) => result.item)
            .toList();
      } else {
        // Use simple contains search
        _filteredApps = _allApps
            .where((app) => app.matchesQuery(_searchQuery))
            .take(AppConstants.maxSearchResults)
            .toList();
      }
      
      // Add to search history
      _addToSearchHistory(_searchQuery);
    }
    
    notifyListeners();
  }

  void _addToSearchHistory(String query) {
    if (query.isEmpty) return;
    
    _searchHistory.remove(query); // Remove if already exists
    _searchHistory.insert(0, query); // Add to beginning
    
    // Limit history size
    if (_searchHistory.length > AppConstants.maxSearchHistory) {
      _searchHistory = _searchHistory.take(AppConstants.maxSearchHistory).toList();
    }
    
    // Save to preferences
    _prefs?.setStringList(AppConstants.searchHistoryKey, _searchHistory);
  }

  Future<bool> launchApp(AppInfo app) async {
    try {
      final launched = await InstalledApps.startApp(app.packageName);
      if (launched == true) {
        // Update usage statistics
        app.launchCount++;
        app.lastLaunchTime = DateTime.now().millisecondsSinceEpoch;
        
        // Re-sort apps
        _sortApps();
        _updateMostUsedApps();
        
        // Save data
        await _saveAppData();
        
        // Clear search
        clearSearch();
        
        // Provide haptic feedback
        HapticFeedback.lightImpact();
        
        notifyListeners();
        return true;
      }
    } catch (e) {
      debugPrint('Error launching app ${app.packageName}: $e');
    }
    return false;
  }

  void toggleFavorite(AppInfo app) {
    app.isFavorite = !app.isFavorite;
    _sortApps();
    _updateFavoriteApps();
    _saveAppData();
    notifyListeners();
  }

  void _updateFavoriteApps() {
    _favoriteApps = _allApps.where((app) => app.isFavorite).toList();
  }

  void _updateMostUsedApps() {
    _mostUsedApps = _allApps
        .where((app) => app.launchCount > 0)
        .take(AppConstants.maxMostUsedApps)
        .toList();
  }

  void clearSearch() {
    _searchQuery = '';
    _filteredApps = List.from(_allApps);
    notifyListeners();
  }

  void clearSearchHistory() {
    _searchHistory.clear();
    _prefs?.setStringList(AppConstants.searchHistoryKey, []);
    notifyListeners();
  }

  // Settings methods
  void setFuzzySearch(bool enabled) {
    _fuzzySearchEnabled = enabled;
    _prefs?.setBool(AppConstants.fuzzySearchKey, enabled);
    if (_searchQuery.isNotEmpty) {
      _performSearch(); // Re-search with new settings
    }
    notifyListeners();
  }

  void setShowMostUsed(bool show) {
    _showMostUsed = show;
    _prefs?.setBool(AppConstants.showMostUsedKey, show);
    notifyListeners();
  }

  void setAutoFocus(bool autoFocus) {
    _autoFocus = autoFocus;
    _prefs?.setBool(AppConstants.autoFocusKey, autoFocus);
    notifyListeners();
  }

  Future<void> refreshApps() async {
    await _loadApps();
  }

  // Get apps for display based on current state
  List<AppInfo> getDisplayApps() {
    if (_searchQuery.isNotEmpty) {
      return _filteredApps;
    } else if (_showMostUsed && _mostUsedApps.isNotEmpty) {
      return _mostUsedApps;
    } else {
      return _favoriteApps.isNotEmpty ? _favoriteApps : _allApps.take(20).toList();
    }
  }

  @override
  void dispose() {
    _debounceTimer?.cancel();
    super.dispose();
  }
} 