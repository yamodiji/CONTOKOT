import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/app_info.dart';
import '../providers/theme_provider.dart';
import '../utils/constants.dart';

class AppItemWidget extends StatefulWidget {
  final AppInfo app;
  final VoidCallback onTap;
  final VoidCallback onLongPress;
  final bool animationsEnabled;

  const AppItemWidget({
    super.key,
    required this.app,
    required this.onTap,
    required this.onLongPress,
    this.animationsEnabled = true,
  });

  @override
  State<AppItemWidget> createState() => _AppItemWidgetState();
}

class _AppItemWidgetState extends State<AppItemWidget>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _scaleAnimation;
  bool _isPressed = false;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      duration: Duration(
        milliseconds: widget.animationsEnabled ? AppConstants.animationDurationMs : 0,
      ),
      vsync: this,
    );
    _scaleAnimation = Tween<double>(
      begin: 1.0,
      end: 0.95,
    ).animate(CurvedAnimation(
      parent: _animationController,
      curve: Curves.easeInOut,
    ));
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  void _onTapDown(TapDownDetails details) {
    if (widget.animationsEnabled) {
      setState(() {
        _isPressed = true;
      });
      _animationController.forward();
    }
  }

  void _onTapUp(TapUpDetails details) {
    if (widget.animationsEnabled) {
      setState(() {
        _isPressed = false;
      });
      _animationController.reverse();
    }
  }

  void _onTapCancel() {
    if (widget.animationsEnabled) {
      setState(() {
        _isPressed = false;
      });
      _animationController.reverse();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<ThemeProvider>(
      builder: (context, themeProvider, child) {
        return AnimatedBuilder(
          animation: _scaleAnimation,
          builder: (context, child) {
            return Transform.scale(
              scale: _scaleAnimation.value,
              child: GestureDetector(
                onTap: widget.onTap,
                onLongPress: widget.onLongPress,
                onTapDown: _onTapDown,
                onTapUp: _onTapUp,
                onTapCancel: _onTapCancel,
                child: Container(
                  decoration: BoxDecoration(
                    color: _isPressed
                        ? themeProvider.getSurfaceColor(context).withOpacity(0.5)
                        : Colors.transparent,
                    borderRadius: BorderRadius.circular(AppConstants.borderRadius),
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      // App icon
                      Container(
                        width: themeProvider.iconSize,
                        height: themeProvider.iconSize,
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(
                            themeProvider.iconSize * 0.2,
                          ),
                          boxShadow: [
                            BoxShadow(
                              color: Colors.black.withOpacity(0.1),
                              blurRadius: 4,
                              offset: const Offset(0, 2),
                            ),
                          ],
                        ),
                        child: ClipRRect(
                          borderRadius: BorderRadius.circular(
                            themeProvider.iconSize * 0.2,
                          ),
                          child: widget.app.icon != null
                              ? Image.memory(
                                  widget.app.icon!,
                                  fit: BoxFit.cover,
                                  errorBuilder: (context, error, stackTrace) {
                                    return _buildFallbackIcon(themeProvider);
                                  },
                                )
                              : _buildFallbackIcon(themeProvider),
                        ),
                      ),
                      
                      const SizedBox(height: AppConstants.paddingSmall),
                      
                      // App name
                      Text(
                        widget.app.displayName,
                        style: TextStyle(
                          color: themeProvider.getTextColor(context),
                          fontSize: 12,
                          fontWeight: FontWeight.w500,
                        ),
                        textAlign: TextAlign.center,
                        maxLines: 2,
                        overflow: TextOverflow.ellipsis,
                      ),
                      
                      // Favorite indicator
                      if (widget.app.isFavorite)
                        Padding(
                          padding: const EdgeInsets.only(top: 2),
                          child: Icon(
                            Icons.favorite,
                            size: 12,
                            color: Colors.red.withOpacity(0.7),
                          ),
                        ),
                    ],
                  ),
                ),
              ),
            );
          },
        );
      },
    );
  }

  Widget _buildFallbackIcon(ThemeProvider themeProvider) {
    return Container(
      width: themeProvider.iconSize,
      height: themeProvider.iconSize,
      decoration: BoxDecoration(
        color: themeProvider.getSurfaceColor(context),
        borderRadius: BorderRadius.circular(
          themeProvider.iconSize * 0.2,
        ),
      ),
      child: Icon(
        Icons.android,
        size: themeProvider.iconSize * 0.6,
        color: themeProvider.getTextColor(context).withOpacity(0.5),
      ),
    );
  }
} 