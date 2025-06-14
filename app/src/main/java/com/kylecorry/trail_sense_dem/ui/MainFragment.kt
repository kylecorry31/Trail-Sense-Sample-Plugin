package com.kylecorry.trail_sense_dem.ui

import com.kylecorry.andromeda.fragments.XmlReactiveFragment
import com.kylecorry.andromeda.fragments.useBackgroundMemo
import com.kylecorry.andromeda.views.toolbar.Toolbar
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.sol.units.DistanceUnits
import com.kylecorry.trail_sense_dem.R
import com.kylecorry.trail_sense_dem.infrastructure.DEM

class MainFragment : XmlReactiveFragment(R.layout.fragment_main) {
    override fun onUpdate() {
        val titleView = useView<Toolbar>(R.id.home_title)

        val context = useAndroidContext()
        val elevation = useBackgroundMemo {
            DEM.getElevation(context, Coordinate(41.905, -71.695))
        }

        useEffect(titleView, elevation) {
            titleView.title.text = elevation?.convertTo(DistanceUnits.Feet)?.distance?.toString()
        }
    }
}