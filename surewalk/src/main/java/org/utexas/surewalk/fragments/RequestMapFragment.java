package org.utexas.surewalk.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.ui.IconGenerator;

import org.utexas.surewalk.classes.OnFragmentReadyListener;

import java.util.ArrayList;

public class RequestMapFragment extends SupportMapFragment {
	
	private static final LatLng UT_TOWER = new LatLng(30.285706, -97.739423);
	private Marker destMarker, startMarker;
    private double mLat, mLon, mStartLat, mStartLon;
    private GoogleMap mMap;
    public boolean mPointPicked = false;
    private final ArrayList<LatLng> mHole = new ArrayList<LatLng>();
    private IconGenerator iconGen;

	public RequestMapFragment() {}
	
	public static RequestMapFragment newInstance(GoogleMapOptions options, String title) {
		RequestMapFragment fragment = new RequestMapFragment();

        Bundle args = new Bundle();
        args.putParcelable("MapOptions", options); //obtained by decompiling google-play-services.jar
        args.putString("title", title);
        fragment.setArguments(args);

        return fragment;
    }

	@Override
	public void onActivityCreated(Bundle args) {
		super.onActivityCreated(args);
		if (super.getMap() != null)
			setupMap();
	}
	
	private void setupMap() {
		mMap = super.getMap();
		// UT mHole (subject to change)

        // Template
//        mHole.add(new LatLng(30., -97.));     //

        // Dirty Martin's -> West campus -> MLK
		mHole.add(new LatLng(30.2933, -97.7418));       // 28th and Guad
		mHole.add(new LatLng(30.29365, -97.74720));     // 28th west end (to lamar-ish)
        mHole.add(new LatLng(30.29327, -97.74698));     // --smoothing
        mHole.add(new LatLng(30.29274, -97.74703));     // --smoothing
        mHole.add(new LatLng(30.29228, -97.74740));     // --smoothing
        mHole.add(new LatLng(30.29212, -97.74760));     // --smoothing
		mHole.add(new LatLng(30.29153, -97.75061));     // Longview and 26th
        mHole.add(new LatLng(30.29117, -97.75103));     // --smoothing
        mHole.add(new LatLng(30.29063, -97.75147));     // --smoothing
        mHole.add(new LatLng(30.28979, -97.75216));     // --smoothing
        mHole.add(new LatLng(30.28905, -97.75259));     // --smoothing
        mHole.add(new LatLng(30.28854, -97.75278));     // 24th and Lamar
        mHole.add(new LatLng(30.28673, -97.75319));     // --smoothing
        mHole.add(new LatLng(30.28640, -97.75321));     // --smoothing
        mHole.add(new LatLng(30.28558, -97.75318));     // --smoothing
        mHole.add(new LatLng(30.28530, -97.75312));     // --smoothing
        mHole.add(new LatLng(30.28483, -97.75294));     // 19th and Lamar
        mHole.add(new LatLng(30.28432, -97.75263));     // --smoothing
        mHole.add(new LatLng(30.28377, -97.75226));     // MLK and Lamar
        mHole.add(new LatLng(30.28377, -97.75170));     // --smoothing
        mHole.add(new LatLng(30.28379, -97.75135));     // --smoothing
        mHole.add(new LatLng(30.28395, -97.75004));     // 19th and MLK
        mHole.add(new LatLng(30.28388, -97.74945));     // MLK and Robbins Rode
        mHole.add(new LatLng(30.28351, -97.74827));     // MLK and San gabriel

        // Erwin center/nursing school/hospitals
        mHole.add(new LatLng(30.27970, -97.73450));     // 15th and MLK
        mHole.add(new LatLng(30.27585, -97.73601));     // 15th and Trinity
        mHole.add(new LatLng(30.27459, -97.73182));     // 15th and I-35

        // RR -> North campus -> Dirty Martin's
        mHole.add(new LatLng(30.278554, -97.730493));   //MLK and I-35
        mHole.add(new LatLng(30.28325, -97.72791));     // RR and littlefield
		mHole.add(new LatLng(30.28718, -97.726761));    // Dean Keeton and Red River
        mHole.add(new LatLng(30.28835, -97.72903));     // DK and medical arts
        mHole.add(new LatLng(30.28907, -97.72997));     // --smoothing
        mHole.add(new LatLng(30.28933, -97.73124));     // DK and Eastwoods park
        mHole.add(new LatLng(30.28921, -97.73272));     // DK and parkplace
        mHole.add(new LatLng(30.28949, -97.73322));     // Parkplace and Harris
        mHole.add(new LatLng(30.29005, -97.73446));     // San Jacinto and park pl.
        mHole.add(new LatLng(30.29064, -97.73454));     // --smoothing
        mHole.add(new LatLng(30.29124, -97.73492));     // --smoothing
        mHole.add(new LatLng(30.29172, -97.73533));     // --smoothing
        mHole.add(new LatLng(30.29215, -97.73576));     // --smoothing
        mHole.add(new LatLng(30.29240, -97.73591));     // --smoothing
        mHole.add(new LatLng(30.29267, -97.73597));     // --smoothing
        mHole.add(new LatLng(30.29315, -97.73602));     // 30th and Speedway
        mHole.add(new LatLng(30.29432, -97.73745));     // Technically also 30th and speedway
        mHole.add(new LatLng(30.29605, -97.74110));     // 30th and Fruth
        mHole.add(new LatLng(30.29429, -97.74227));     // Dirty Martin's

		mMap.setMyLocationEnabled(false);
		mMap.setBuildingsEnabled(true);
		mMap.getUiSettings().setCompassEnabled(true);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UT_TOWER, 14.5f));
		
		iconGen = new IconGenerator(getActivity());
		iconGen.setStyle(IconGenerator.STYLE_BLUE);
		final BitmapDescriptor destinationDesc = 
		        BitmapDescriptorFactory.fromBitmap(iconGen.makeIcon("Destination"));
		mMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng point) {

                if(destMarker != null) {
                    destMarker.remove();
                }

                double distance = haversine(30.28768, -97.74039, point.latitude, point.longitude);

                if (distance > 1.5) {
                    Toast.makeText(getActivity(), "Out of range", Toast.LENGTH_SHORT).show();
                    return;
                }

				destMarker = mMap.addMarker(new MarkerOptions()
				            .icon(destinationDesc)
							.position(point)
							.draggable(false)
							.visible(true));

                ((OnFragmentReadyListener) getActivity()).onFragmentReady(true);


				if(!containsPoint(mHole, point)) {
                    createAlertDialog(true);
				}

				mLat = point.latitude;
				mLon = point.longitude;
				mPointPicked = true;
			}
			
		});
		//setup/draw bounding polygon here
		PolygonOptions bounds = new PolygonOptions();
		
		//polygon over the earth
		bounds.add(new LatLng(85f, 179.9f))
			  .add(new LatLng(85f, 90f))
			  .add(new LatLng(85f, 0f))
			  .add(new LatLng(85f, -90f))
			  .add(new LatLng(85f, -180f))
			  .add(new LatLng(0f, -180f))
			  .add(new LatLng(-85f, -180f))
			  .add(new LatLng(-85f, -90f))
			  .add(new LatLng(-85f, 0f))
			  .add(new LatLng(-85f, 90f))
			  .add(new LatLng(-85f, 179.9f));
		
		bounds.addHole(mHole);
		bounds.fillColor(0x400F7183)
			  .strokeColor(0x500F7183)
			  .strokeWidth(3f);
		mMap.addPolygon(bounds);

	}
	
	public void setStartLoc(double lat, double lng) {
	    if (startMarker != null) {
	        startMarker.remove();
	    }
	    
	    startMarker = mMap.addMarker(new MarkerOptions()
	                .icon(BitmapDescriptorFactory.fromBitmap(iconGen.makeIcon("Start")))
                    .position(new LatLng(lat, lng))
                    .draggable(false)
                    .visible(true));

        mStartLat = lat;
        mStartLon = lng;
	}
	
    public double[] getCoordinates(){
        if (mPointPicked) {
            return new double[]{mLat, mLon};
        }
        else {
            return null;
        }
    }

    /**
     * creates alert dialog for this fragment
     * @param to a grammatical thing so that it says "to" when the put an invalid end destination
     *           and "from" when there is an invalid start location
     */
    public void createAlertDialog(boolean to) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle("Location");
        if (to)
            b.setMessage("SURE Walk might not be able to walk you to this destination");
        else
            b.setMessage("SURE Walk might not be able to walk you from this location");
        b.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = b.create();
        dialog.show();
    }

    //DARK STACKOVERFLOW MAGIC
    public boolean containsPoint(ArrayList<LatLng> bounds, LatLng pt) {
        int i, j = 0;
        boolean c = false;
        for(i = 0, j = bounds.size() - 1; i < bounds.size(); j = i++) {
            if(((bounds.get(i).latitude > pt.latitude) != (bounds.get(j).latitude > pt.latitude)) &&
                    (pt.longitude < (bounds.get(j).longitude - bounds.get(i).longitude) * (pt.latitude - bounds.get(i).latitude) / (bounds.get(j).latitude - bounds.get(i).latitude) + bounds.get(i).longitude))
                c = !c;
        }
        return c;
    }

    public ArrayList<LatLng> getHole() {
        return mHole;
    }

    public LatLng getStartLoc() {
        return startMarker.getPosition();
    }

    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6372.8; // In kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}
