package vn.edu.uit.devorbit.admin.ui.courses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.core.designsystem.*
import vn.edu.uit.devorbit.admin.ui.components.ObsidianDivider
import vn.edu.uit.devorbit.admin.data.remote.dto.*
import vn.edu.uit.devorbit.admin.ui.theme.*

private val typeFilterOptions = listOf(
    FilterOption(id = "", label = "T\u1ea5t c\u1ea3"),
    FilterOption(id = "DAI_CUONG", label = "\u0110\u1ea1i c\u01b0\u01a1ng"),
    FilterOption(id = "CO_SO", label = "C\u01a1 s\u1edf"),
    FilterOption(id = "CO_SO_NGANH", label = "C\u01a1 s\u1edf ng\u00e0nh"),
    FilterOption(id = "CHUYEN_NGANH", label = "Chuy\u00ean ng\u00e0nh"),
)

// ═════════════════════════════════════════════════════════════════════════════
//  COURSES SCREEN
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun CoursesScreen(
    viewModel: CoursesViewModel = hiltViewModel(),
    onCourseClick: (Long) -> Unit = {},
    onRelationshipsClick: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show delete result as snackbar
    LaunchedEffect(state.deleteResult) {
        if (state.deleteResult != null) {
            snackbarHostState.showSnackbar(
                message = "\u0110\u00e3 xo\u00e1 m\u00f4n h\u1ecdc th\u00e0nh c\u00f4ng",
                duration = SnackbarDuration.Short,
            )
            viewModel.clearDeleteResult()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            // ── Header ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "M\u00f4n h\u1ecdc",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    Text(
                        text = "${state.filteredCourses.size} m\u00f4n h\u1ecdc",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }

            // ── Search ──────────────────────────────────────────────────────
            AdminSearchField(
                query = state.searchQuery,
                onQueryChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier.padding(horizontal = 16.dp),
                placeholder = "T\u00ecm ki\u1ebfm theo m\u00e3 ho\u1eb7c t\u00ean\u2026",
            )

            Spacer(Modifier.height(8.dp))

            // ── Type Filter Chips ──────────────────────────────────────────
            FilterChipGroup(
                options = typeFilterOptions,
                selectedId = state.selectedTypeFilter ?: "",
                onSelected = { viewModel.setTypeFilter(it.ifBlank { null }) },
            )

            Spacer(Modifier.height(8.dp))

            // ── Action Buttons ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AdminSecondaryButton(
                    text = "Quan h\u1ec7",
                    onClick = onRelationshipsClick,
                    icon = Icons.Rounded.Share,
                    modifier = Modifier.weight(1f),
                )
                AdminPrimaryButton(
                    text = "Th\u00eam m\u00f4n h\u1ecdc",
                    onClick = { viewModel.openEditor() },
                    icon = Icons.Rounded.Add,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Content ─────────────────────────────────────────────────────
            when {
                state.isFirstLoading -> {
                    InitialLoading()
                }

                state.error != null && state.allCourses.isEmpty() -> {
                    ErrorState(
                        subtitle = state.error,
                        onRetry = { viewModel.loadCourses() },
                    )
                }

                state.filteredCourses.isEmpty() -> {
                    if (state.searchQuery.isNotBlank() || state.selectedTypeFilter != null) {
                        NoResultsState(query = state.searchQuery)
                    } else {
                        EmptyState(
                            title = "Ch\u01b0a c\u00f3 m\u00f4n h\u1ecdc",
                            subtitle = "Nh\u1ea5n Th\u00eam m\u00f4n h\u1ecdc \u0111\u1ec3 t\u1ea1o m\u00f4n m\u1edbi",
                            icon = Icons.Rounded.MenuBook,
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(state.filteredCourses, key = { it.id }) { course ->
                            CourseItem(
                                course = course,
                                onClick = { onCourseClick(course.id) },
                                onEdit = { viewModel.openEditor(course.id) },
                                onDelete = { viewModel.requestDelete(course) },
                            )
                        }
                    }
                }
            }
        }

        // Snackbar for transient messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        )
    }

    // ── Full-screen Editor Dialog ─────────────────────────────────────────
    if (state.showEditor) {
        CourseEditorDialog(
            editingDetail = state.editingDetail,
            isLoadingDetail = state.isLoadingDetail,
            isSubmitting = state.isSubmitting,
            submitError = state.submitError,
            onDismiss = { viewModel.closeEditor() },
            onCreate = { viewModel.createCourse(it) },
            onUpdate = { id, req -> viewModel.updateCourse(id, req) },
        )
    }

    // ── Delete Confirmation Sheet ────────────────────────────────────────
    ConfirmationSheet(
        visible = state.deleteTarget != null,
        title = "Xo\u00e1 m\u00f4n h\u1ecdc",
        message = buildString {
            val course = state.deleteTarget
            if (course != null) {
                append("B\u1ea1n c\u00f3 ch\u1eafc mu\u1ed1n xo\u00e1 \"${course.name ?: course.code}\"?")
            }
            append("\n\nK\u00eanh th\u1ea3o lu\u1eadn li\u00ean quan s\u1ebd b\u1ecb v\u00f4 hi\u1ec7u ho\u00e1 v\u00e0 kh\u00f4ng th\u1ec3 kh\u00f4i ph\u1ee5c.")
        },
        confirmLabel = "Xo\u00e1",
        confirmDanger = true,
        loading = state.isDeleting,
        onDismiss = { viewModel.cancelDelete() },
        onConfirm = { viewModel.confirmDelete() },
    )
}

// ═════════════════════════════════════════════════════════════════════════════
//  COURSE ITEM
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun CourseItem(
    course: CourseSummaryResponse,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = ObsidianShape.md,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Code — monospace
                Text(
                    text = course.code ?: "\u2014",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                    ),
                    color = UITBlue,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(2.dp))

                // Name
                Text(
                    text = course.name ?: "Ch\u01b0a c\u00f3 t\u00ean",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Spacer(Modifier.height(6.dp))

                // Credits + Badge + Management unit
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "${course.credits} t\u00edn ch\u1ec9",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                    )

                    if (course.loaiMonHoc != null) {
                        StatusBadge(
                            label = subjectTypeLabel(course.loaiMonHoc),
                            type = StatusType.INFO,
                        )
                    }

                    if (!course.managementUnit.isNullOrBlank()) {
                        Text(
                            text = course.managementUnit,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                        )
                    }
                }

                // Repo count hint
                if (course.repoCount > 0) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "${course.repoCount} kho",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                    )
                }
            }

            // Context menu
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        Icons.Rounded.MoreVert,
                        contentDescription = "Tu\u0300y cho\u0323n",
                        tint = TextMuted,
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("S\u01b0\u0309a") },
                        onClick = {
                            showMenu = false
                            onEdit()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                    DropdownMenuItem(
                        text = {
                            Text("Xoa\u0301", color = Danger)
                        },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Delete,
                                contentDescription = null,
                                tint = Danger,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  FULL-SCREEN COURSE EDITOR
// ═════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseEditorDialog(
    editingDetail: CourseDetailResponse?,
    isLoadingDetail: Boolean,
    isSubmitting: Boolean,
    submitError: String?,
    onDismiss: () -> Unit,
    onCreate: (AdminCourseUpsertRequest) -> Unit,
    onUpdate: (Long, AdminCourseUpsertRequest) -> Unit,
) {
    val isEditMode = editingDetail != null
    val title = if (isEditMode) "S\u01b0\u0309a m\u00f4n ho\u0323c" else "Th\u00eam m\u00f4n ho\u0323c"

    // ── Form state (re-initialized when editingDetail changes) ─────────────
    var code by remember(editingDetail) { mutableStateOf(editingDetail?.code ?: "") }
    var name by remember(editingDetail) { mutableStateOf(editingDetail?.name ?: "") }
    var nameEn by remember(editingDetail) { mutableStateOf(editingDetail?.nameEn ?: "") }
    var credits by remember(editingDetail) { mutableStateOf(if (editingDetail != null) editingDetail.credits.toString() else "") }
    var subjectType by remember(editingDetail) { mutableStateOf(editingDetail?.subjectType ?: "DAI_CUONG") }
    var lectureHours by remember(editingDetail) { mutableStateOf(editingDetail?.theoryHours?.toString() ?: "") }
    var practiceHours by remember(editingDetail) { mutableStateOf(editingDetail?.practiceHours?.toString() ?: "") }
    var managementUnit by remember(editingDetail) { mutableStateOf(editingDetail?.managementUnit ?: "") }
    var isOpen by remember(editingDetail) { mutableStateOf(editingDetail?.isOpen ?: true) }
    var codeOld by remember(editingDetail) { mutableStateOf(editingDetail?.codeOld ?: "") }
    var equivalentMH by remember(editingDetail) { mutableStateOf(editingDetail?.equivalentMH ?: "") }
    var prerequisiteMH by remember(editingDetail) { mutableStateOf(editingDetail?.prerequisiteMH ?: "") }
    var previousMH by remember(editingDetail) { mutableStateOf(editingDetail?.previousMH ?: "") }
    var description by remember(editingDetail) { mutableStateOf(editingDetail?.description ?: "") }
    var learningObjectives by remember(editingDetail) { mutableStateOf(editingDetail?.learningObjectives ?: "") }
    var gradingCriteria by remember(editingDetail) { mutableStateOf(editingDetail?.gradingCriteria ?: "") }

    val formValid = code.isNotBlank()
            && name.isNotBlank()
            && credits.isNotBlank()
            && credits.toIntOrNull() != null
            && subjectType.isNotBlank()

    val canDismiss = !isSubmitting

    val subjectTypeOptions = remember {
        listOf("DAI_CUONG", "CO_SO", "CO_SO_NGANH", "CHUYEN_NGANH")
    }

    Dialog(
        onDismissRequest = { if (canDismiss) onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = canDismiss,
            dismissOnClickOutside = false,
        ),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = AppBackground,
            topBar = {
                AdminTopBar(
                    title = title,
                    showBack = canDismiss,
                    onBack = { if (canDismiss) onDismiss() },
                    onNotificationClick = null,
                    onLogout = null,
                    actions = {
                        AdminPrimaryButton(
                            text = if (isEditMode) "C\u00e2\u0323p nh\u00e2\u0323t" else "Ta\u0323o",
                            loading = isSubmitting,
                            enabled = formValid && !isLoadingDetail,
                            onClick = {
                                val request = AdminCourseUpsertRequest(
                                    code = code.trim(),
                                    name = name.trim(),
                                    nameEn = nameEn.trim().ifBlank { null },
                                    credits = credits.toIntOrNull() ?: 0,
                                    lectureHours = lectureHours.toIntOrNull(),
                                    practiceHours = practiceHours.toIntOrNull(),
                                    subjectType = subjectType,
                                    isOpen = isOpen,
                                    managementUnit = managementUnit.trim().ifBlank { null },
                                    codeOld = codeOld.trim().ifBlank { null },
                                    equivalentMH = equivalentMH.trim().ifBlank { null },
                                    prerequisiteMH = prerequisiteMH.trim().ifBlank { null },
                                    previousMH = previousMH.trim().ifBlank { null },
                                    description = description.trim().ifBlank { null },
                                    learningObjectives = learningObjectives.trim().ifBlank { null },
                                    gradingCriteria = gradingCriteria.trim().ifBlank { null },
                                )
                                if (isEditMode) {
                                    onUpdate(editingDetail.id, request)
                                } else {
                                    onCreate(request)
                                }
                            },
                        )
                    },
                )
            },
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                when {
                    isLoadingDetail -> InitialLoading()
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                        ) {
                            // Submit error banner
                            if (submitError != null) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = DangerSoft,
                                    shape = ObsidianShape.sm,
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            Icons.Rounded.ErrorOutline,
                                            contentDescription = null,
                                            tint = Danger,
                                            modifier = Modifier.size(18.dp),
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = submitError,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Danger,
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                            }

                            // ── Section 1: Thông tin cơ bản ──────────────
                            SectionHeader(title = "Th\u00f4ng tin c\u01a1 ba\u0309n")

                            FormField(
                                value = code,
                                onValueChange = { code = it },
                                label = "Ma\u0303 m\u00f4n",
                                required = true,
                                placeholder = "VD: SE101",
                            )
                            FormField(
                                value = name,
                                onValueChange = { name = it },
                                label = "T\u00ean m\u00f4n ho\u0323c",
                                required = true,
                                placeholder = "T\u00ean ti\u1ebfng Vi\u1ec7t",
                            )
                            FormField(
                                value = nameEn,
                                onValueChange = { nameEn = it },
                                label = "T\u00ean ti\u00ea\u0301ng Anh",
                                placeholder = "Optional",
                            )

                            ObsidianDivider()
                            Spacer(Modifier.height(8.dp))

                            // ── Section 2: Phân loại ─────────────────────
                            SectionHeader(title = "Ph\u00e2n loa\u0323i")

                            Text(
                                text = "Loa\u0323i m\u00f4n ho\u0323c *",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 8.dp),
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                subjectTypeOptions.forEach { opt ->
                                    FilterChip(
                                        selected = opt == subjectType,
                                        onClick = { subjectType = opt },
                                        label = {
                                            Text(
                                                subjectTypeLabel(opt),
                                                style = MaterialTheme.typography.labelSmall,
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = UITBlueSoft,
                                            selectedLabelColor = UITBlue,
                                            containerColor = Surface,
                                            labelColor = TextSecondary,
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            borderColor = Border,
                                            selectedBorderColor = UITBlueBorder,
                                            enabled = true,
                                            selected = opt == subjectType,
                                        ),
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))

                            FormField(
                                value = credits,
                                onValueChange = { credits = it.filter { c -> c.isDigit() } },
                                label = "S\u00f4\u0301 ti\u0301n chi\u0309",
                                required = true,
                                keyboardType = KeyboardType.Number,
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                FormField(
                                    value = lectureHours,
                                    onValueChange = { lectureHours = it.filter { c -> c.isDigit() } },
                                    label = "Gi\u01a1\u0300 ly\u0301 thuy\u00ea\u0301t",
                                    keyboardType = KeyboardType.Number,
                                    modifier = Modifier.weight(1f),
                                )
                                FormField(
                                    value = practiceHours,
                                    onValueChange = { practiceHours = it.filter { c -> c.isDigit() } },
                                    label = "Gi\u01a1\u0300 th\u01b0\u0323c ha\u0300nh",
                                    keyboardType = KeyboardType.Number,
                                    modifier = Modifier.weight(1f),
                                )
                            }

                            ObsidianDivider()
                            Spacer(Modifier.height(8.dp))

                            // ── Section 3: Quản lý ───────────────────────
                            SectionHeader(title = "Qua\u0309n ly\u0301")

                            FormField(
                                value = managementUnit,
                                onValueChange = { managementUnit = it },
                                label = "\u0110\u01a1n vi\u0323 qua\u0309n ly\u0301",
                                placeholder = "VD: Khoa KHMT",
                            )
                            FormField(
                                value = codeOld,
                                onValueChange = { codeOld = it },
                                label = "Ma\u0303 cu\u0303",
                                placeholder = "VD: SE101-old",
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Checkbox(
                                    checked = isOpen,
                                    onCheckedChange = { isOpen = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = UITBlue,
                                        uncheckedColor = Border,
                                    ),
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "\u0110ang m\u01a1\u0309",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                )
                            }

                            ObsidianDivider()
                            Spacer(Modifier.height(8.dp))

                            // ── Section 4: Quan hệ ────────────────────────
                            SectionHeader(title = "Quan h\u00ea\u0323 m\u00f4n ho\u0323c")

                            FormField(
                                value = equivalentMH,
                                onValueChange = { equivalentMH = it },
                                label = "M\u00f4n t\u01b0\u01a1ng \u0111\u01b0\u01a1ng",
                                placeholder = "Ma\u0303 m\u00f4n, VD: SE100",
                            )
                            FormField(
                                value = prerequisiteMH,
                                onValueChange = { prerequisiteMH = it },
                                label = "M\u00f4n ti\u00ean quy\u00ea\u0301t",
                                placeholder = "Ma\u0303 m\u00f4n, VD: SE100",
                            )
                            FormField(
                                value = previousMH,
                                onValueChange = { previousMH = it },
                                label = "M\u00f4n tr\u01b0\u01a1\u0301c",
                                placeholder = "Ma\u0303 m\u00f4n, VD: SE100",
                            )

                            ObsidianDivider()
                            Spacer(Modifier.height(8.dp))

                            // ── Section 5: Nội dung ──────────────────────
                            SectionHeader(title = "N\u00f4\u0323i dung")

                            MultilineField(
                                value = description,
                                onValueChange = { description = it },
                                label = "M\u00f4 ta\u0309",
                            )
                            Spacer(Modifier.height(12.dp))
                            MultilineField(
                                value = learningObjectives,
                                onValueChange = { learningObjectives = it },
                                label = "Mu\u0323c ti\u00eau ho\u0323c t\u00e2\u0323p",
                            )
                            Spacer(Modifier.height(12.dp))
                            MultilineField(
                                value = gradingCriteria,
                                onValueChange = { gradingCriteria = it },
                                label = "Ti\u00eau chi\u0301 \u0111a\u0301nh gia\u0301",
                            )

                            // Bottom spacer so content isn't hidden behind nav
                            Spacer(Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  REUSABLE FORM BUILDING BLOCKS
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = UITBlue,
        modifier = Modifier.padding(vertical = 12.dp),
    )
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
) {
    val displayLabel = if (required) "$label *" else label
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(displayLabel) },
        placeholder = if (placeholder != null) {
            { Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = TextMuted) }
        } else null,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        singleLine = singleLine,
        shape = ObsidianShape.sm,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = UITBlue,
            unfocusedBorderColor = Border,
            cursorColor = UITBlue,
            focusedContainerColor = Surface,
            unfocusedContainerColor = Surface,
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    )
}

@Composable
private fun MultilineField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        minLines = 3,
        maxLines = 8,
        shape = ObsidianShape.sm,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = UITBlue,
            unfocusedBorderColor = Border,
            cursorColor = UITBlue,
            focusedContainerColor = Surface,
            unfocusedContainerColor = Surface,
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}
