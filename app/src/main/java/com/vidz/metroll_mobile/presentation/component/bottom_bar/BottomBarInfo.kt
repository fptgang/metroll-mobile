import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.vector.ImageVector
import com.vidz.base.navigation.DestinationRoutes


@SuppressLint("SupportAnnotationUsage")
data class BottomBarInfo(
    val id: String,
    val route: String? = null,
    val name: String? = null,
    val lightIconUrl: String = "",
    val lightSelectIconUrl: String = "",
    val darkIconUrl: String = "",
    val darkSelectIconUrl: String = "",
    val numberItemOfPage: String = "",
    @DrawableRes val defaultLightIcon: ImageVector? = null,
    @DrawableRes val defaultLightSelectIcon: ImageVector? = null,
    @DrawableRes val defaultDarkIcon: ImageVector? = null,
    @DrawableRes val defaultDarkSelectIcon: ImageVector? = null
) {

    companion object {
        val defaults = listOf(
            BottomBarInfo(
                id = "1",
                route = DestinationRoutes.HOME_SCREEN_ROUTE,
                name = "Trang chủ",
                defaultLightIcon = Icons.Filled.Home,
                defaultLightSelectIcon = Icons.Filled.Home,
                defaultDarkIcon = Icons.Filled.Home,
                defaultDarkSelectIcon = Icons.Filled.Home,
            ),
            BottomBarInfo(
                id = "2",
                route = DestinationRoutes.ROUTE_MANAGEMENT_SCREEN_ROUTE,
                name = "Tuyến đường",
                defaultLightIcon = Icons.Filled.Train,
                defaultLightSelectIcon = Icons.Filled.Train,
                defaultDarkIcon = Icons.Filled.Train,
                defaultDarkSelectIcon = Icons.Filled.Train,
            ),
            BottomBarInfo(
                id = "3",
                route = DestinationRoutes.TICKET_PURCHASE_SCREEN_ROUTE,
                name = "Mua vé",
                defaultLightIcon = Icons.Filled.ConfirmationNumber,
                defaultLightSelectIcon = Icons.Filled.ConfirmationNumber,
                defaultDarkIcon = Icons.Filled.ConfirmationNumber,
                defaultDarkSelectIcon = Icons.Filled.ConfirmationNumber,
            ),
            BottomBarInfo(
                id = "4",
                route = DestinationRoutes.ACCOUNT_PROFILE_SCREEN_ROUTE,
                name = "Tài khoản",
                defaultLightIcon = Icons.Filled.AccountCircle,
                defaultLightSelectIcon = Icons.Filled.AccountCircle,
                defaultDarkIcon = Icons.Filled.AccountCircle,
                defaultDarkSelectIcon = Icons.Filled.AccountCircle,
            ),
        )
    }
}
