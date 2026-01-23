package mk.digital.kmpshowcase.presentation.di

import mk.digital.kmpshowcase.presentation.component.barcode.CodeGenerator
import mk.digital.kmpshowcase.presentation.component.imagepicker.ImagePickerViewModel
import mk.digital.kmpshowcase.presentation.screen.calendar.CalendarViewModel
import mk.digital.kmpshowcase.presentation.screen.database.DatabaseViewModel
import mk.digital.kmpshowcase.presentation.screen.home.HomeViewModel
import mk.digital.kmpshowcase.presentation.screen.login.LoginViewModel
import mk.digital.kmpshowcase.presentation.screen.networking.NetworkingViewModel
import mk.digital.kmpshowcase.presentation.screen.notifications.NotificationsViewModel
import mk.digital.kmpshowcase.presentation.screen.platformapis.PlatformApisViewModel
import mk.digital.kmpshowcase.presentation.screen.register.RegisterViewModel
import mk.digital.kmpshowcase.presentation.screen.scanner.ScannerViewModel
import mk.digital.kmpshowcase.presentation.screen.settings.SettingsViewModel
import mk.digital.kmpshowcase.presentation.screen.storage.StorageViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    factoryOf(::CodeGenerator)

    viewModelOf(::HomeViewModel)
    viewModelOf(::NetworkingViewModel)
    viewModelOf(::StorageViewModel)
    viewModelOf(::PlatformApisViewModel)
    viewModelOf(::ScannerViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ImagePickerViewModel)
    viewModelOf(::DatabaseViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
}
