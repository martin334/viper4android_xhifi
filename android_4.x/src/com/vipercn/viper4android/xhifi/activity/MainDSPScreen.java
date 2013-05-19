package com.vipercn.viper4android.xhifi.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.vipercn.viper4android.xhifi.R;
import com.vipercn.viper4android.xhifi.preference.EqualizerPreference;
import com.vipercn.viper4android.xhifi.preference.SummariedListPreference;

public final class MainDSPScreen extends PreferenceFragment
{
	private final OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener()
	{
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
		{
			Log.i("ViPER4Android_XHiFi", "Update key = " + key);

			if ("viper4android.headphone_xhifi.fireq".equals(key))
			{
				String newValue = sharedPreferences.getString(key, null);
				if (!"custom".equals(newValue))
				{
					Editor e = sharedPreferences.edit();
					e.putString("viper4android.headphone_xhifi.fireq.custom", newValue);
					e.commit();
					EqualizerPreference eq = (EqualizerPreference) getPreferenceScreen().findPreference("viper4android.headphone_xhifi.fireq.custom");
					eq.refreshFromPreference();
				}
			}

			if ("viper4android.headphone_xhifi.fireq.custom".equals(key))
			{
				String newValue = sharedPreferences.getString(key, null);
				String desiredValue = "custom";
				SummariedListPreference preset = (SummariedListPreference) getPreferenceScreen().findPreference("viper4android.headphone_xhifi.fireq");
				for (CharSequence entry : preset.getEntryValues())
				{
					if (entry.equals(newValue))
					{
						desiredValue = newValue;
						break;
					}
				}
				if (!desiredValue.equals(preset.getEntry()))
				{
					Editor e = sharedPreferences.edit();
					e.putString("viper4android.headphone_xhifi.fireq", desiredValue);
					e.commit();
					preset.refreshFromPreference();
				}
			}

			getActivity().sendBroadcast(new Intent(ViPER4Android_XHiFi.ACTION_UPDATE_PREFERENCES));
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		String config = getArguments().getString("config");

		getPreferenceManager().setSharedPreferencesName(ViPER4Android_XHiFi.SHARED_PREFERENCES_BASENAME + "." + config);

		try
		{
			int xmlId = R.xml.class.getField(config + "_preferences").getInt(null);
			addPreferencesFromResource(xmlId);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
	}
}
