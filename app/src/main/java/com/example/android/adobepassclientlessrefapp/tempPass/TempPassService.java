//package com.example.android.adobepassclientlessrefapp.tempPass;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.CountDownTimer;
//import android.os.IBinder;
//
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//
//import org.joda.time.DateTime;
//
///**
// * Class taken from NBC Sports application
// */
//// TODO: Delete class if not using
//public class TempPassService extends Service {
//
//	static final public String BROADCAST_ACTION = "com.nbc.nbcsports.services.TempPassAction";
//    static final public String EXPIRED = "temppass_expired";
//    static final public String COUNTER = "temppass_counter";
//    static final public String AUTHN = "authN";
//	private Intent intent;
//	private TempPassCounter timer;
//	private long secondsLeft;
//	private LocalBroadcastManager broadcaster;
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		broadcaster = LocalBroadcastManager.getInstance(getApplicationContext());
//		intent = new Intent(TempPassService.BROADCAST_ACTION);
//	}
//
//	@Override
//	public int onStartCommand(final Intent intent, final int flags, final int startId) {
//		super.onStartCommand(intent, flags, startId);
//		if (intent != null && intent.getExtras() != null) {
//
//			String authN = intent.getExtras().getString(AUTHN);
////            authN = authN.replaceAll("/", "-");
////            authN = authN.replaceFirst(" ", "T");
////            authN = authN.replaceFirst(" GMT ", "");
////			  DateTime expireTime = DateTime.parse(authN, ISODateTimeFormat.dateTimeNoMillis().withZone(DateTimeZone.getDefault()));
//			long expiry = Long.parseLong(authN);
//			long future = expiry - DateTime.now().getMillis();
//
//            if (timer != null) timer.cancel();
//			timer = new TempPassCounter(future, 1000);
//            timer.start();
//		}
//		return Service.START_NOT_STICKY;
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		if (timer != null) timer.cancel();
//		stopSelf();
//	}
//
//	@Override
//	public IBinder onBind(final Intent intent) {
//		return null;
//	}
//
//	private class TempPassCounter extends CountDownTimer {
//		public TempPassCounter(final long millisInFuture, final long countDownInterval) {
//			super(millisInFuture, countDownInterval);
//		}
//
//		@Override
//		public void onFinish() {
//			secondsLeft = 0;
//            intent.putExtra(EXPIRED, true);
//            broadcaster.sendBroadcast(intent);
//			stopSelf();
//		}
//
//		@Override
//		public void onTick(final long millisUntilFinished) {
//			secondsLeft = millisUntilFinished / 1000;
//            intent.putExtra(COUNTER, secondsLeft);
//            broadcaster.sendBroadcast(intent);
//		}
//	}
//}
