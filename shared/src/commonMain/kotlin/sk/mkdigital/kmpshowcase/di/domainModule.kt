package sk.mkdigital.kmpshowcase.di

import sk.mkdigital.kmpshowcase.domain.useCase.note.CreateRemoteNoteUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.note.DeleteRemoteNoteUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.note.GetRemoteNotesUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.note.UpdateRemoteNoteUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.TrackButtonClickUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.analytics.TrackScreenUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.auth.SignInUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.auth.SignInWithTokenUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.auth.SignOutUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.auth.SignUpUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.biometric.AuthenticateWithBiometricUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.biometric.IsBiometricEnabledUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.calendar.GetTodayDateUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.flashlight.IsFlashlightAvailableUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.flashlight.ToggleFlashlightUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.flashlight.TurnOffFlashlightUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.location.GetLastKnownLocationUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.location.ObserveLocationUpdatesUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.notes.DeleteAllNotesUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.notes.DeleteNoteUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.notes.InsertNoteUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.notes.ObserveNotesUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.notes.SearchNotesUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.notes.UpdateNoteUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.notifications.CancelAllNotificationsUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.notifications.GetPushPermissionStatusUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.notifications.LogPushTokenUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.notifications.ObservePushNotificationsUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.notifications.ObservePushTokenUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.notifications.RefreshPushTokenUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.notifications.ShowLocalNotificationUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.settings.GetThemeModeUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.settings.SetThemeModeUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.storage.ClearCacheUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.storage.LoadStorageDataUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.storage.ObserveStorageDataUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.storage.SetPersistentCounterUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.storage.SetSessionCounterUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::TrackScreenUseCase)
    factoryOf(::TrackButtonClickUseCase)
    factoryOf(::GetRemoteNotesUseCase)
    factoryOf(::CreateRemoteNoteUseCase)
    factoryOf(::UpdateRemoteNoteUseCase)
    factoryOf(::DeleteRemoteNoteUseCase)
    factoryOf(::LoadStorageDataUseCase)
    factoryOf(::ObserveStorageDataUseCase)
    factoryOf(::SetSessionCounterUseCase)
    factoryOf(::SetPersistentCounterUseCase)
    factoryOf(::ClearCacheUseCase)
    factoryOf(::GetThemeModeUseCase)
    factoryOf(::SetThemeModeUseCase)
    factoryOf(::ObserveNotesUseCase)
    factoryOf(::SearchNotesUseCase)
    factoryOf(::InsertNoteUseCase)
    factoryOf(::UpdateNoteUseCase)
    factoryOf(::DeleteNoteUseCase)
    factoryOf(::DeleteAllNotesUseCase)
    factoryOf(::GetTodayDateUseCase)
    factoryOf(::IsBiometricEnabledUseCase)
    factoryOf(::AuthenticateWithBiometricUseCase)
    factoryOf(::SignInUseCase)
    factoryOf(::SignInWithTokenUseCase)
    factoryOf(::SignOutUseCase)
    factoryOf(::SignUpUseCase)

    factoryOf(::GetLastKnownLocationUseCase)
    factoryOf(::ObserveLocationUpdatesUseCase)

    factoryOf(::IsFlashlightAvailableUseCase)
    factoryOf(::ToggleFlashlightUseCase)
    factoryOf(::TurnOffFlashlightUseCase)

    factoryOf(::GetPushPermissionStatusUseCase)
    factoryOf(::ObservePushTokenUseCase)
    factoryOf(::ObservePushNotificationsUseCase)
    factoryOf(::RefreshPushTokenUseCase)
    factoryOf(::LogPushTokenUseCase)
    factoryOf(::ShowLocalNotificationUseCase)
    factoryOf(::CancelAllNotificationsUseCase)

}
