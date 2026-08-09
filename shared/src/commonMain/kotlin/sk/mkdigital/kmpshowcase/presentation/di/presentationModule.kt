package sk.mkdigital.kmpshowcase.presentation.di

import sk.mkdigital.kmpshowcase.presentation.component.barcode.CodeGenerator
import sk.mkdigital.kmpshowcase.presentation.component.imagepicker.ImagePickerViewModel
import sk.mkdigital.kmpshowcase.presentation.screen.calendar.CalendarViewModel
import sk.mkdigital.kmpshowcase.presentation.screen.database.DatabaseViewModel
import sk.mkdigital.kmpshowcase.presentation.screen.home.HomeViewModel
import sk.mkdigital.kmpshowcase.presentation.screen.signIn.SignInViewModel
import sk.mkdigital.kmpshowcase.presentation.screen.networking.NetworkingViewModel
import sk.mkdigital.kmpshowcase.presentation.screen.notifications.NotificationsViewModel
import sk.mkdigital.kmpshowcase.presentation.screen.platformapis.PlatformApisViewModel
import sk.mkdigital.kmpshowcase.presentation.screen.signUp.SignUpViewModel
import sk.mkdigital.kmpshowcase.presentation.screen.scanner.ScannerViewModel
import sk.mkdigital.kmpshowcase.presentation.screen.settings.SettingsViewModel
import sk.mkdigital.kmpshowcase.presentation.screen.storage.StorageViewModel
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
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)
}
