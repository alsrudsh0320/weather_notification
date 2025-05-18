package com.alsrudsh0320.weather_notification

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsrudsh0320.weather_notification.data_load.RegionDataLoader
import com.alsrudsh0320.weather_notification.data_load.RegionPoint
import com.alsrudsh0320.weather_notification.databinding.ActivityMainBinding
import com.alsrudsh0320.weather_notification.layout_adapter.RegionSuggestionAdapter
import com.alsrudsh0320.weather_notification.location_time.CoordinateConverter
import com.alsrudsh0320.weather_notification.location_time.LocationHelper
import com.alsrudsh0320.weather_notification.utils.Constants.TAG
import com.alsrudsh0320.weather_notification.weather_api_manager.WeatherApiManager
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    private lateinit var regionsList: List<RegionPoint>
    private lateinit var suggestionAdapter: RegionSuggestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()      // 화면 전체를 활용하기 위함
        setContentView(binding.root)
        
        // EdgeToEdge 레이아웃 모드에서 사용자 설정 XML코드의 padding을 적용하기 위한 코드
        val mainView = findViewById<View>(R.id.main)
        val origPadding = intArrayOf(
            mainView.paddingLeft,
            mainView.paddingTop,
            mainView.paddingRight,
            mainView.paddingBottom
        )
        // 시스템 바 높이 만큼 적절히 안쪽여백 주기 + 추가 XML코드 설정
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                origPadding[0] + sys.left,    // left
                origPadding[1] + sys.top,     // top
                origPadding[2] + sys.right,   // right
                origPadding[3] + sys.bottom   // bottom
            )
            insets
        }

        // JSON에서 로드된 지역 정보를 리스트로 가져옴
        regionsList = RegionDataLoader.loadRegions(this)
        Log.d(TAG, "Loaded regions: $regionsList")

        // RecyclerView, Adapter 준비 + item 눌렀을 때 callback
        suggestionAdapter = RegionSuggestionAdapter(emptyList()) { region ->
            // 사용자가 항목을 탭했을 때: 리스트 숨김

            binding.rvSuggestions.visibility = View.GONE
            lifecycleScope.launch {
                val response = WeatherApiManager.instance.fetchShortTermForecastData(this@MainActivity, region = region)
                Log.d(TAG, "response: $response")
            }

        }
        binding.rvSuggestions.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = suggestionAdapter
        }

        // SearchView 문자 변화 감지해서 필터링
        binding.svRegionSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val q = newText?.trim().orEmpty()
                if (q.isEmpty()) {
                    binding.rvSuggestions.visibility = View.GONE
                } else {
                    // 1~3단계 합쳐서 검색
                    val filtered = regionsList.filter { rp ->
                        // level1, level2, level3을 공백으로 결합
                        val fullName = listOf(rp.regionLevel1, rp.regionLevel2, rp.regionLevel3)
                            .filter { it.isNotBlank() }
                            .joinToString(" ")
                        fullName.contains(q, ignoreCase = true)
                    }.take(15)  // 미리보기 최대 10개

                    suggestionAdapter.update(filtered)
                    binding.rvSuggestions.visibility =
                        if (filtered.isEmpty()) View.GONE else View.VISIBLE
                }
                return true
            }
        })


        // 테스트 버튼을 눌렀을 때
        binding.btnTest.setOnClickListener {
            if (checkLocationPermission()) {
                lifecycleScope.launch {
                    val response = WeatherApiManager.instance.fetchShortTermForecastData(this@MainActivity)
                    Log.d(TAG, "response: $response")
                }
            } else {
                // 권한이 없을 때, "다시 묻지 않음" 옵션 선택 여부에 따라 처리
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission.ACCESS_FINE_LOCATION)) {
                    showPermissionSettingsDialog()
                } else {
                    requestLocationPermission()
                }
            }
        }

        // 테스트 버튼 2 클릭 리스너 설정: RegionDataLoader 기능 검증
        binding.btnTest2.setOnClickListener {

        }

    }// onCreate


    ///////////////// 여기서 부터 사용자 위치 권한 관련 ///////////////

    // 위치 권한 check
    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 사용자에게 권한 요청 (다이얼로그)
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    // 사용자가 권한 요청에 응답한 후 호출되는 콜백 함수
    // 일반적으로 사용자가 한번 허용한 후에는 호출 X
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lifecycleScope.launch {
                    val response = WeatherApiManager().fetchShortTermForecastData(this@MainActivity)
                    Log.d(TAG, "response: $response")
                }

            } else {
                Toast.makeText(this, "위치 권한이 거부되었습니다", Toast.LENGTH_SHORT).show()
                showPermissionSettingsDialog()
            }
        }
    }

    private fun showPermissionSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("권한 필요")
            .setMessage("이 앱은 위치 권한이 필수적입니다. 권한을 수동으로 설정해주세요.")
            .setPositiveButton("설정으로 이동") { dialog, _ ->
                dialog.dismiss()
                // 앱 설정 화면으로 이동하는 인텐트
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    ///////////////// 여기 까지 사용자 위치 권한 관련 ///////////////

}