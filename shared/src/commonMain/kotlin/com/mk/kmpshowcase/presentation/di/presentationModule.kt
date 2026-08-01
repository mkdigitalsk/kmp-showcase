package com.mk.kmpshowcase.presentation.di

import com.mk.kmpshowcase.presentation.component.barcode.CodeGenerator
import com.mk.kmpshowcase.presentation.component.imagepicker.ImagePickerViewModel
import com.mk.kmpshowcase.presentation.screen.calendar.CalendarViewModel
import com.mk.kmpshowcase.presentation.screen.database.DatabaseViewModel
import com.mk.kmpshowcase.presentation.screen.home.HomeViewModel
import com.mk.kmpshowcase.presentation.screen.login.LoginViewModel
import com.mk.kmpshowcase.presentation.screen.networking.NetworkingViewModel
import com.mk.kmpshowcase.presentation.screen.notifications.NotificationsViewModel
import com.mk.kmpshowcase.presentation.screen.platformapis.PlatformApisViewModel
import com.mk.kmpshowcase.presentation.screen.register.RegisterViewModel
import com.mk.kmpshowcase.presentation.screen.scanner.ScannerViewModel
import com.mk.kmpshowcase.presentation.screen.settings.SettingsViewModel
import com.mk.kmpshowcase.presentation.screen.storage.StorageViewModel
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
