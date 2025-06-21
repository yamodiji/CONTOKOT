import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/app_info.dart';
import '../providers/theme_provider.dart';
import '../providers/settings_provider.dart';
import '../widgets/app_item_widget.dart';
import '../utils/constants.dart';

class AppGridWidget extends StatelessWidget {
  final List<AppInfo> apps;
  final Function(AppInfo) onAppTap;
  final Function(AppInfo) onAppLongPress;

  const AppGridWidget({
    super.key,
    required this.apps,
    required this.onAppTap,
    required this.onAppLongPress,
  });

  @override
  Widget build(BuildContext context) {
    if (apps.isEmpty) {
      return _buildEmptyState(context);
    }

    return Consumer2<ThemeProvider, SettingsProvider>(
      builder: (context, themeProvider, settingsProvider, child) {
        // Calculate grid dimensions based on screen size and icon size
        final screenWidth = MediaQuery.of(context).size.width;
        final iconSize = settingsProvider.iconSize;
        final itemWidth = iconSize + AppConstants.paddingMedium * 2;
        final crossAxisCount = (screenWidth / itemWidth).floor().clamp(3, 6);

        return Padding(
          padding: const EdgeInsets.symmetric(
            horizontal: AppConstants.paddingSmall,
          ),
          child: CustomScrollView(
            physics: const BouncingScrollPhysics(),
            slivers: [
              // Performance optimized grid
              SliverGrid(
                gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: crossAxisCount,
                  childAspectRatio: 0.9,
                  crossAxisSpacing: AppConstants.paddingSmall,
                  mainAxisSpacing: AppConstants.paddingSmall,
                ),
                delegate: SliverChildBuilderDelegate(
                  (context, index) {
                    final app = apps[index];
                    return AppItemWidget(
                      app: app,
                      onTap: () => onAppTap(app),
                      onLongPress: () => onAppLongPress(app),
                      animationsEnabled: settingsProvider.animationsEnabled,
                    );
                  },
                  childCount: apps.length,
                  addAutomaticKeepAlives: false,
                  addRepaintBoundaries: true,
                ),
              ),
              
              // Bottom padding
              const SliverToBoxAdapter(
                child: SizedBox(height: AppConstants.paddingLarge),
              ),
            ],
          ),
        );
      },
    );
  }

  Widget _buildEmptyState(BuildContext context) {
    return Consumer<ThemeProvider>(
      builder: (context, themeProvider, child) {
        return Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                Icons.search_off,
                size: 64,
                color: themeProvider.getTextColor(context).withOpacity(0.3),
              ),
              const SizedBox(height: AppConstants.paddingMedium),
              Text(
                'No apps found',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  color: themeProvider.getTextColor(context).withOpacity(0.6),
                ),
              ),
              const SizedBox(height: AppConstants.paddingSmall),
              Text(
                'Try adjusting your search',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: themeProvider.getTextColor(context).withOpacity(0.4),
                ),
              ),
            ],
          ),
        );
      },
    );
  }
} 