package com.password.mobile.android.feature.home.screen

import com.passbolt.mobile.android.common.DomainProvider
import com.passbolt.mobile.android.common.InitialsProvider
import com.passbolt.mobile.android.common.search.SearchableMatcher
import com.passbolt.mobile.android.commontest.TestCoroutineLaunchContext
import com.passbolt.mobile.android.core.commonfolders.usecase.db.GetLocalFolderDetailsUseCase
import com.passbolt.mobile.android.core.commonfolders.usecase.db.GetLocalResourcesAndFoldersUseCase
import com.passbolt.mobile.android.core.commonfolders.usecase.db.GetLocalSubFolderResourcesFilteredUseCase
import com.passbolt.mobile.android.core.commonfolders.usecase.db.GetLocalSubFoldersForFolderUseCase
import com.passbolt.mobile.android.core.commongroups.usecase.db.GetLocalGroupsWithShareItemsCountUseCase
import com.passbolt.mobile.android.core.fulldatarefresh.FullDataRefreshExecutor
import com.passbolt.mobile.android.core.idlingresource.DeleteResourceIdlingResource
import com.passbolt.mobile.android.core.mvp.coroutinecontext.CoroutineLaunchContext
import com.passbolt.mobile.android.core.otpcore.TotpParametersProvider
import com.passbolt.mobile.android.core.resources.actions.ResourceCommonActionsInteractor
import com.passbolt.mobile.android.core.resources.actions.ResourcePropertiesActionsInteractor
import com.passbolt.mobile.android.core.resources.actions.ResourceUpdateActionsInteractor
import com.passbolt.mobile.android.core.resources.actions.SecretPropertiesActionsInteractor
import com.passbolt.mobile.android.core.resources.usecase.RebuildResourceTablesUseCase
import com.passbolt.mobile.android.core.resources.usecase.ResourceInteractor
import com.passbolt.mobile.android.core.resources.usecase.db.GetLocalResourcesFilteredByTagUseCase
import com.passbolt.mobile.android.core.resources.usecase.db.GetLocalResourcesUseCase
import com.passbolt.mobile.android.core.resources.usecase.db.GetLocalResourcesWithGroupUseCase
import com.passbolt.mobile.android.core.resources.usecase.db.GetLocalResourcesWithTagUseCase
import com.passbolt.mobile.android.core.resourcetypes.ResourceTypeFactory
import com.passbolt.mobile.android.core.secrets.usecase.decrypt.parser.SecretParser
import com.passbolt.mobile.android.core.tags.usecase.db.GetLocalTagsUseCase
import com.passbolt.mobile.android.feature.home.screen.HomeContract
import com.passbolt.mobile.android.feature.home.screen.HomePresenter
import com.passbolt.mobile.android.mappers.HomeDisplayViewMapper
import com.passbolt.mobile.android.otpmoremenu.usecase.CreateOtpMoreMenuModelUseCase
import com.passbolt.mobile.android.resourcemoremenu.usecase.CreateResourceMoreMenuModelUseCase
import com.passbolt.mobile.android.storage.usecase.accountdata.GetSelectedAccountDataUseCase
import com.passbolt.mobile.android.storage.usecase.preferences.GetHomeDisplayViewPrefsUseCase
import com.passbolt.mobile.android.storage.usecase.selectedaccount.GetSelectedAccountUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.dsl.module
import org.mockito.kotlin.mock

internal val resourcesInteractor = mock<ResourceInteractor>()
internal val getSelectedAccountDataUseCase = mock<GetSelectedAccountDataUseCase>()
internal val getSelectedAccountUseCase = mock<GetSelectedAccountUseCase>()
internal val fetchAndUpdateDatabaseUseCase = mock<RebuildResourceTablesUseCase>()
internal val mockSecretParser = mock<SecretParser>()
internal val mockResourceTypeFactory = mock<ResourceTypeFactory>()
internal val mockGetLocalResourcesUseCase = mock<GetLocalResourcesUseCase>()
internal val mockGetSubFoldersUseCase = mock<GetLocalSubFoldersForFolderUseCase>()
internal val mockGetSubFoldersResourcesUseCase = mock<GetLocalSubFolderResourcesFilteredUseCase>()
internal val mockGetLocalResourcesAndFoldersUseCase = mock<GetLocalResourcesAndFoldersUseCase>()
internal val mockGetLocalTagsUseCase = mock<GetLocalTagsUseCase>()
internal val mockGetLocalResourcesWithTagsUseCase = mock<GetLocalResourcesWithTagUseCase>()
internal val mockGetLocalGroupsWithItemCountUseCase = mock<GetLocalGroupsWithShareItemsCountUseCase>()
internal val mockGetLocalResourcesWithGroupsUseCase = mock<GetLocalResourcesWithGroupUseCase>()
internal val mockGetLocalResourcesFilteredByTagUseCase = mock<GetLocalResourcesFilteredByTagUseCase>()
internal val mockGetHomeDisplayPrefsUseCase = mock<GetHomeDisplayViewPrefsUseCase>()
internal val mockGetLocalFolderUseCase = mock<GetLocalFolderDetailsUseCase>()
internal val mockCreateResourceMoreMenuModelUseCase = mock<CreateResourceMoreMenuModelUseCase>()
internal val mockTotpParametersProvider = mock<TotpParametersProvider>()
internal val mockCreateOtpMoreMenuModelUseCase = mock<CreateOtpMoreMenuModelUseCase>()
internal val mockSecretPropertiesActionsInteractor = mock<SecretPropertiesActionsInteractor>()
internal val mockResourcePropertiesActionsInteractor = mock<ResourcePropertiesActionsInteractor>()
internal val mockResourceCommonActionsInteractor = mock<ResourceCommonActionsInteractor>()
internal val mockResourceUpdateActionsInteractor = mock<ResourceUpdateActionsInteractor>()

@ExperimentalCoroutinesApi
val testHomeModule = module {
    factory { resourcesInteractor }
    factory { getSelectedAccountDataUseCase }
    factory { fetchAndUpdateDatabaseUseCase }
    factory { InitialsProvider() }
    factory<CoroutineLaunchContext> { TestCoroutineLaunchContext() }
    factory { SearchableMatcher() }
    factory { mockSecretParser }
    factory { mockResourceTypeFactory }
    factory { mockCreateResourceMoreMenuModelUseCase }
    factory { HomeDisplayViewMapper() }
    factory { DomainProvider() }
    single { mock<FullDataRefreshExecutor>() }
    single { DeleteResourceIdlingResource() }
    factory<HomeContract.Presenter> {
        HomePresenter(
            coroutineLaunchContext = get(),
            getSelectedAccountDataUseCase = get(),
            searchableMatcher = get(),
            getLocalResourcesUseCase = mockGetLocalResourcesUseCase,
            getLocalResourcesFilteredByTag = mockGetLocalResourcesFilteredByTagUseCase,
            getLocalSubFoldersForFolderUseCase = mockGetSubFoldersUseCase,
            getLocalResourcesAndFoldersUseCase = mockGetLocalResourcesAndFoldersUseCase,
            getLocalResourcesFiltered = mockGetSubFoldersResourcesUseCase,
            getLocalTagsUseCase = mockGetLocalTagsUseCase,
            getLocalResourcesWithTagUseCase = mockGetLocalResourcesWithTagsUseCase,
            getLocalGroupsWithShareItemsCountUseCase = mockGetLocalGroupsWithItemCountUseCase,
            getLocalResourcesWithGroupsUseCase = mockGetLocalResourcesWithGroupsUseCase,
            getHomeDisplayViewPrefsUseCase = mockGetHomeDisplayPrefsUseCase,
            homeModelMapper = get(),
            domainProvider = get(),
            getLocalFolderUseCase = mockGetLocalFolderUseCase,
            deleteResourceIdlingResource = get(),
            totpParametersProvider = mockTotpParametersProvider,
            resourceTypeFactory = mockResourceTypeFactory
        )
    }
    factory { mockResourceCommonActionsInteractor }
    factory { mockResourcePropertiesActionsInteractor }
    factory { mockSecretPropertiesActionsInteractor }
    factory { mockResourceUpdateActionsInteractor }
}
