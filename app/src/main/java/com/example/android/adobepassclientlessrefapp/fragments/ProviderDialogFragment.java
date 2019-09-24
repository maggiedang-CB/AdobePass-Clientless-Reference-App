package com.example.android.adobepassclientlessrefapp.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

//import androidx.fragment.app.DialogFragment;

import com.nbcsports.leapsdk.authentication.adobepass.api.MvpdListAPI.Mvpd;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


import com.example.android.adobepassclientlessrefapp.R;

import butterknife.BindView;

/**
 * Taken from the NBC sports app
 */
public class ProviderDialogFragment extends DialogFragment {

	private static final String SCREEN_NAME = "Select Provider";

	private static final String ARG_MVPDS = "ARG_MVPDS";
	// set SHOW_MVPD_LOGOS_ONLY_PREMIUM to false to show logos for every provider.
	// set to true to show only premium providers
	private static final boolean SHOW_MVPD_LOGOS_ONLY_PREMIUM = true;

    //TveService pass;
    Picasso picasso;
    //@Inject TrackingHelper trackingHelper;

	private ListView mListView;
	private List<Mvpd> mvpds;
	private LayoutInflater inflater;


	public static ProviderDialogFragment getInstance(final ArrayList<Mvpd> mvpds) {
		final ProviderDialogFragment frag = new ProviderDialogFragment();
		final Bundle args = new Bundle();
		args.putSerializable(ProviderDialogFragment.ARG_MVPDS, mvpds);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		getActivity().finish();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mvpds = (List<Mvpd>) getArguments().getSerializable(ProviderDialogFragment.ARG_MVPDS);

	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		this.inflater = inflater;
		getDialog().setTitle(SCREEN_NAME);

		final View v = inflater.inflate(R.layout.fragment_provider_picker, container, false);
		mListView = (ListView) v.findViewById(android.R.id.list);

		// Set button listener for the cancel button
		Button btnMvpdListCancel = v.findViewById(R.id.btn_mvpd_frag_cancel);
		btnMvpdListCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		List<Mvpd> premiumMvpd = new ArrayList<Mvpd>();
		final List<Mvpd> standardMvpd = new ArrayList<Mvpd>();
		final List<Mvpd> preferred = new ArrayList<Mvpd>();
		for (int i = 0; i < mvpds.size(); i++) {
			final Mvpd mvpd = mvpds.get(i);
//			if (isPremiumMvpd(mvpd) && !pass.isTempPass(mvpd)) {
//				premiumMvpd.add(mvpd);
//			} else if (isStandardMvpd(mvpd) && !pass.isTempPass(mvpd)) {
//				standardMvpd.add(mvpd);
//			}
			//leapp 4874
			// TODO: do sorting of premium and standard mvpd later
			standardMvpd.add(mvpd);

		}
		//premiumMvpd = sortPremiumMvpds(premiumMvpd, pass.getCurrentChannel().getMvpdPremium());
		preferred.addAll(premiumMvpd);
		preferred.addAll(standardMvpd);
		mListView.setAdapter(new ProviderAdapter(preferred));
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
//				((SelectProviderActivity) getActivity()).setSelectedMvpd((Mvpd) parent.getItemAtPosition(position));
//				dismiss();
			}
		});

	}

	private List<Mvpd> sortPremiumMvpds(final List<Mvpd> preferred, List<String> mvpdPremium ) {
        final List<Mvpd> newPreferred = new ArrayList<>();
		for ( final String mvpdStringId : mvpdPremium ) {

			for ( final Mvpd m : preferred ) {
				if ( mvpdStringId.equals( m.getId() ) ) {
					newPreferred.add( m );
				}
			}
		}
		return newPreferred;
	}

//	public boolean isPremiumMvpd(final Mvpd item) {
//		final Channel currentChannel = pass.getCurrentChannel();
//		if (currentChannel == null) return false;
//
//		for (final String mvpd : currentChannel.getMvpdPremium()) {
//			if (mvpd.equals(item.getId())) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public boolean isStandardMvpd(final Mvpd item) {
//		final Channel currentChannel = pass.getCurrentChannel();
//		if (currentChannel == null) return false;
//
//		for (final String mvpd : currentChannel.getMvpdStandard()) {
//			if (mvpd.equals(item.getId())) {
//				return true;
//			}
//		}
//		return false;
//	}

	private class ProviderAdapter extends BaseAdapter {
		private final List<Mvpd> mItems;

		public ProviderAdapter(final List<Mvpd> items) {
			mItems = items;
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(final int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(final int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.adapter_provider, parent, false);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final Mvpd item = (Mvpd) getItem(position);
			if (!SHOW_MVPD_LOGOS_ONLY_PREMIUM) {
				holder.displayName.setVisibility(View.GONE);
				holder.logo.setVisibility(View.VISIBLE);
				if (! TextUtils.isEmpty(item.getLogoUrl())) {
					picasso.load(item.getLogoUrl())
							.resizeDimen(R.dimen.premium_logo_width, R.dimen.premium_logo_height)
							.centerInside()
							.placeholder(R.drawable.placeholder_image)
							.into(holder.logo);
				} else {
					Log.d("picasso", "image null "+item.getLogoUrl());
				}
			} else {
                Log.d("picasso", "not PREMIUM");

                holder.displayName.setVisibility(View.VISIBLE);
				holder.logo.setVisibility(View.GONE);
			}
			holder.displayName.setText(item.getDisplayName());
			return convertView;
		}
	}

	private static class ViewHolder {
		public TextView displayName;
		public ImageView logo;

		public ViewHolder(final View convertView) {
			displayName = (TextView) convertView.findViewById(R.id.display_name);
			logo = (ImageView) convertView.findViewById(R.id.logo);
		}
	}
}
