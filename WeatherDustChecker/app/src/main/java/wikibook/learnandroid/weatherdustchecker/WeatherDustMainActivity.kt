package wikibook.learnandroid.weatherdustchecker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class WeatherDustMainActivity : AppCompatActivity() {
    private lateinit var mPager: ViewPager2

    // (1)
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    // (2)
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    // (3)
    private val PERMISSION_REQUEST_CODE : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_dust_main_activity)
        supportActionBar?.hide()

        mPager = findViewById(R.id.pager)

        // (4)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // (5)
        locationListener = object : LocationListener {
            // (6)
            override fun onLocationChanged(location: Location) {
                lat = location.latitude
                lon = location.longitude

                // (7)
                locationManager.removeUpdates(this)

                // (8)
                val pagerAdapter = MyPagerAdapter()
                mPager.adapter = pagerAdapter
                mPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrollStateChanged(p0: Int) {}
                    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
                    override fun onPageSelected(position: Int) {
                        if(position == 0) {
                            // (1)
                            // https://stackoverflow.com/questions/55728719/get-current-fragment-with-viewpager2
                            val fragment = supportFragmentManager.findFragmentByTag("f" + position) as WeatherPageFragment
                            fragment.startAnimation()
                        } else if(position == 1) {
                            // 미세먼지 정보 프래그먼트로 변경된 시점에도 같은 방식으로 startAnimation 메서드를 호출
                            val fragment = supportFragmentManager.findFragmentByTag("f" + position) as DustPageFragment
                            fragment.startAnimation()
                        }
                    }
                })
            }
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        // (9)
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // (10)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
        } else {
            // (11)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        }
    }

    // (12)
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when(requestCode) {
            // (13)
            PERMISSION_REQUEST_CODE -> {
                var allPermissionsGranted = true
                for(result in grantResults) {
                    allPermissionsGranted = (result == PackageManager.PERMISSION_GRANTED)
                    if(!allPermissionsGranted) break
                }
                if(allPermissionsGranted) {
                    // (14)
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
                } else {
                    // (15)
                    Toast.makeText(applicationContext, "위치 정보 제공 동의가 필요합니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private inner class MyPagerAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> WeatherPageFragment.newInstance(lat, lon)
                1 -> DustPageFragment.newInstance(lat, lon)
                else -> {
                    throw Exception("페이지가 존재하지 않음.")
                }
            }
        }
    }
}
