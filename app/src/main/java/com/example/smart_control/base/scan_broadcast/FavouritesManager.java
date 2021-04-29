package com.example.smart_control.base.scan_broadcast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Build;

import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.example.smart_control.Myapp;
import com.example.smart_control.R;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FavouritesManager {
        private final Context context;
        private final SharedPreferences sharedPreferences;
        private final RegTypeManager regTypeManager;
        private final Set<String> favouriteRegTypes;

        public FavouritesManager(Context context) {
            this.context = context;
            sharedPreferences = context.getSharedPreferences("DEFAULT", Context.MODE_PRIVATE);
            regTypeManager = Myapp.getRegTypeManager(context);
            favouriteRegTypes = new HashSet<>(sharedPreferences.getAll().keySet());
        }

        public boolean isFavourite(String regType) {
            return favouriteRegTypes.contains(regType);
        }

        public void addToFavourites(String regType) {
            boolean success = favouriteRegTypes.add(regType);
            if (success) {
                sharedPreferences.edit().putBoolean(regType, true).apply();
                updateDynamicShortcuts();
            }
        }

        public void removeFromFavourites(String regType) {
            boolean success = favouriteRegTypes.remove(regType);
            if (success) {
                sharedPreferences.edit().remove(regType).apply();
                updateDynamicShortcuts();
            }
        }

        void updateDynamicShortcuts() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
                return;
            }

            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            if (shortcutManager == null) {
                return;
            }

            List<ShortcutInfo> shortcuts = new LinkedList<>();

            for (String regType : favouriteRegTypes) {
                String fullNameRegType = regTypeManager.getRegTypeDescription(regType);
                if (fullNameRegType == null) {
                    fullNameRegType = regType;
                }
                ShortcutInfoCompat newShortcut = new ShortcutInfoCompat.Builder(context, regType)
                        .setShortLabel(fullNameRegType)
                        .setLongLabel(fullNameRegType)
                        .setIcon(IconCompat.createWithResource(context, R.drawable.ic_baseline_account_circle))
                        .setIntent(RegTypeActivity.createIntent(context, regType, Config.LOCAL_DOMAIN).setAction(Intent.ACTION_VIEW))
                        .build();

                shortcuts.add(newShortcut.toShortcutInfo());
            }

            shortcutManager.setDynamicShortcuts(shortcuts);
        }

}
