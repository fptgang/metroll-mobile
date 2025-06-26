package com.vidz.base.navigation

object DestinationRoutes {
    // Root routes for bottom navigation
    const val ROOT_HOME_SCREEN_ROUTE = "root_home_screen_route"
    const val ROOT_AUTH_SCREEN_ROUTE = "root_auth_screen_route"
    const val ROOT_TICKET_SCREEN_ROUTE = "root_ticket_screen_route"
    const val ROOT_ACCOUNT_SCREEN_ROUTE = "root_account_screen_route"
    const val ROOT_MEMBERSHIP_SCREEN_ROUTE = "root_membership_screen_route"
    const val ROOT_STAFF_SCREEN_ROUTE = "root_staff_screen_route"
    const val ROOT_ROUTE_MANAGEMENT_SCREEN_ROUTE = "root_route_management_screen_route"

    // Home/Dashboard routes
    const val HOME_SCREEN_ROUTE = "${ROOT_HOME_SCREEN_ROUTE}/home"
    const val CUSTOMER_HOME_SCREEN_ROUTE = "${ROOT_HOME_SCREEN_ROUTE}/customer_home"
    const val STAFF_HOME_SCREEN_ROUTE = "${ROOT_HOME_SCREEN_ROUTE}/staff_home"

    // Route Management (Quản lý tuyến - ga, tàu, tuyến)
    const val ROUTE_MANAGEMENT_SCREEN_ROUTE = "${ROOT_ROUTE_MANAGEMENT_SCREEN_ROUTE}/route_management"
    const val ROUTE_MAP_SCREEN_ROUTE = "${ROOT_ROUTE_MANAGEMENT_SCREEN_ROUTE}/route_map"
    const val STATION_LIST_SCREEN_ROUTE = "${ROOT_ROUTE_MANAGEMENT_SCREEN_ROUTE}/stations"
    const val TRAIN_SCHEDULE_SCREEN_ROUTE = "${ROOT_ROUTE_MANAGEMENT_SCREEN_ROUTE}/train_schedule"
    const val STATION_DETAIL_SCREEN_BASE_ROUTE = "${ROOT_ROUTE_MANAGEMENT_SCREEN_ROUTE}/station_detail"
    const val STATION_DETAIL_SCREEN_ROUTE = "$STATION_DETAIL_SCREEN_BASE_ROUTE/{stationId}/{stationName}"
    const val TRAIN_DETAIL_SCREEN_BASE_ROUTE = "${ROOT_ROUTE_MANAGEMENT_SCREEN_ROUTE}/train_detail"
    const val TRAIN_DETAIL_SCREEN_ROUTE = "$TRAIN_DETAIL_SCREEN_BASE_ROUTE/{trainId}/{trainNumber}"
    const val ROUTE_DETAIL_SCREEN_BASE_ROUTE = "${ROOT_ROUTE_MANAGEMENT_SCREEN_ROUTE}/route_detail"
    const val ROUTE_DETAIL_SCREEN_ROUTE = "$ROUTE_DETAIL_SCREEN_BASE_ROUTE/{routeId}/{routeName}"

    // Ticket Management (Mua vé, checkin)
    const val TICKET_PURCHASE_SCREEN_ROUTE = "${ROOT_TICKET_SCREEN_ROUTE}/purchase"
    const val TICKET_CART_SCREEN_ROUTE = "${ROOT_TICKET_SCREEN_ROUTE}/cart"
    const val TICKET_SEARCH_SCREEN_ROUTE = "${ROOT_TICKET_SCREEN_ROUTE}/search"
    const val TICKET_PAYMENT_SCREEN_ROUTE = "${ROOT_TICKET_SCREEN_ROUTE}/payment"
    const val TICKET_CONFIRMATION_SCREEN_ROUTE = "${ROOT_TICKET_SCREEN_ROUTE}/confirmation"
    const val MY_TICKETS_SCREEN_ROUTE = "${ROOT_TICKET_SCREEN_ROUTE}/my_tickets"
    const val ORDER_DETAIL_SCREEN_BASE_ROUTE = "${ROOT_TICKET_SCREEN_ROUTE}/order_detail"
    const val ORDER_DETAIL_SCREEN_ROUTE = "$ORDER_DETAIL_SCREEN_BASE_ROUTE/{orderId}"
    const val TICKET_DETAIL_SCREEN_BASE_ROUTE = "${ROOT_TICKET_SCREEN_ROUTE}/ticket_detail"
    const val TICKET_DETAIL_SCREEN_ROUTE = "$TICKET_DETAIL_SCREEN_BASE_ROUTE/{ticketId}"
    const val CHECKIN_SCREEN_ROUTE = "${ROOT_TICKET_SCREEN_ROUTE}/checkin"
    const val QR_TICKET_SCREEN_ROUTE = "${ROOT_TICKET_SCREEN_ROUTE}/qr_ticket"

    // Account Management (Quản lý tài khoản)
    const val ACCOUNT_PROFILE_SCREEN_ROUTE = "${ROOT_ACCOUNT_SCREEN_ROUTE}/profile"
    const val EDIT_PROFILE_SCREEN_ROUTE = "${ROOT_ACCOUNT_SCREEN_ROUTE}/edit_profile"
    const val ACCOUNT_SETTINGS_SCREEN_ROUTE = "${ROOT_ACCOUNT_SCREEN_ROUTE}/settings"
    const val CHANGE_PASSWORD_SCREEN_ROUTE = "${ROOT_ACCOUNT_SCREEN_ROUTE}/change_password"
    const val PAYMENT_METHODS_SCREEN_ROUTE = "${ROOT_ACCOUNT_SCREEN_ROUTE}/payment_methods"
    const val TRAVEL_HISTORY_SCREEN_ROUTE = "${ROOT_ACCOUNT_SCREEN_ROUTE}/travel_history"

    // Membership Management (Quản lý gói thành viên)
    const val MEMBERSHIP_SCREEN_ROUTE = "${ROOT_MEMBERSHIP_SCREEN_ROUTE}/membership"
    const val MEMBERSHIP_PACKAGES_SCREEN_ROUTE = "${ROOT_MEMBERSHIP_SCREEN_ROUTE}/membership_packages"
    const val MEMBERSHIP_PURCHASE_SCREEN_BASE_ROUTE = "${ROOT_MEMBERSHIP_SCREEN_ROUTE}/membership_purchase"
    const val MEMBERSHIP_PURCHASE_SCREEN_ROUTE = "$MEMBERSHIP_PURCHASE_SCREEN_BASE_ROUTE/{packageId}"
    const val MEMBERSHIP_BENEFITS_SCREEN_ROUTE = "${ROOT_MEMBERSHIP_SCREEN_ROUTE}/membership_benefits"

    // Authentication routes
    const val LOGIN_SCREEN_ROUTE = "${ROOT_AUTH_SCREEN_ROUTE}/login"
    const val REGISTER_SCREEN_ROUTE = "${ROOT_AUTH_SCREEN_ROUTE}/register"
    const val FORGOT_PASSWORD_SCREEN_ROUTE = "${ROOT_AUTH_SCREEN_ROUTE}/forgot_password"
    const val OTP_VERIFICATION_SCREEN_ROUTE = "${ROOT_AUTH_SCREEN_ROUTE}/otp_verification"

    // Staff routes (QR scanning only)
    const val STAFF_LOGIN_SCREEN_ROUTE = "${ROOT_STAFF_SCREEN_ROUTE}/staff_login"
    const val STAFF_QR_SCANNER_SCREEN_ROUTE = "${ROOT_STAFF_SCREEN_ROUTE}/qr_scanner"
    const val STAFF_TICKET_VALIDATION_SCREEN_ROUTE = "${ROOT_STAFF_SCREEN_ROUTE}/ticket_validation"
    const val STAFF_SCAN_HISTORY_SCREEN_ROUTE = "${ROOT_STAFF_SCREEN_ROUTE}/scan_history"
}
