package com.aaron.pseplanner.service.implementation;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.aaron.pseplanner.bean.SettingsDto;
import com.aaron.pseplanner.constant.PSEPlannerPreference;
import com.aaron.pseplanner.service.SettingsService;

/**
 * Created by aaron.asuncion on 4/12/2017.
 */

public class DefaultSettingsService implements SettingsService
{
    private SharedPreferences sharedPreferences;

    public DefaultSettingsService(@NonNull Context context)
    {
        this.sharedPreferences = context.getSharedPreferences(PSEPlannerPreference.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    @Override
    public void saveSettings(SettingsDto dto)
    {
        if(dto != null)
        {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putBoolean(PSEPlannerPreference.AUTO_REFRESH.toString(), dto.isAutoRefresh());
            editor.putInt(PSEPlannerPreference.REFRESH_INTERVAL.toString(), dto.getRefreshInterval());
            editor.putBoolean(PSEPlannerPreference.NOTIFY_TARGET_PRICE.toString(), dto.isNotifyTargetPrice());
            editor.putBoolean(PSEPlannerPreference.NOTIFY_STOP_LOSS.toString(), dto.isNotifyStopLoss());
            editor.putBoolean(PSEPlannerPreference.NOTIFY_TIME_STOP.toString(), dto.isNotiftyTimeStop());
            editor.putBoolean(PSEPlannerPreference.NOTIFY_SOUND_EFFECT.toString(), dto.isNotifySoundEffect());
            editor.putString(PSEPlannerPreference.PROXY_HOST.toString(), dto.getProxyHost());
            editor.putInt(PSEPlannerPreference.PROXY_PORT.toString(), dto.getProxyPort());

            editor.apply();
        }
    }

    @Override
    public SettingsDto getSettings()
    {
        if(this.sharedPreferences.contains(PSEPlannerPreference.AUTO_REFRESH.toString())
                && this.sharedPreferences.contains(PSEPlannerPreference.REFRESH_INTERVAL.toString())
                && this.sharedPreferences.contains(PSEPlannerPreference.NOTIFY_TARGET_PRICE.toString())
                && this.sharedPreferences.contains(PSEPlannerPreference.NOTIFY_STOP_LOSS.toString())
                && this.sharedPreferences.contains(PSEPlannerPreference.NOTIFY_TIME_STOP.toString())
                && this.sharedPreferences.contains(PSEPlannerPreference.NOTIFY_SOUND_EFFECT.toString()))
        {
            boolean autoRefresh = this.sharedPreferences.getBoolean(PSEPlannerPreference.AUTO_REFRESH.toString(), false);
            int interval = this.sharedPreferences.getInt(PSEPlannerPreference.REFRESH_INTERVAL.toString(), 30);
            boolean targetPrice = this.sharedPreferences.getBoolean(PSEPlannerPreference.NOTIFY_TARGET_PRICE.toString(), false);
            boolean stopLoss = this.sharedPreferences.getBoolean(PSEPlannerPreference.NOTIFY_STOP_LOSS.toString(), false);
            boolean timeStop = this.sharedPreferences.getBoolean(PSEPlannerPreference.NOTIFY_TIME_STOP.toString(), false);
            boolean soundEffect = this.sharedPreferences.getBoolean(PSEPlannerPreference.NOTIFY_SOUND_EFFECT.toString(), false);
            String proxyHost = this.sharedPreferences.getString(PSEPlannerPreference.PROXY_HOST.toString(), "");
            int proxyPort = this.sharedPreferences.getInt(PSEPlannerPreference.PROXY_PORT.toString(), 0);

            return new SettingsDto(autoRefresh, interval, targetPrice, stopLoss, timeStop, soundEffect, proxyHost, proxyPort);
        }

        return new SettingsDto();
    }
}
