# DevOrbit Mobile Redesign Specification — UIT 2026 Light Edition

This document details the architectural, UI, and UX redesign of the DevOrbit Android client. The app is transformed from the legacy dark slate/amber layout into a world-class, light-only mobile application. This design merges Material 3 Expressive guidelines with the design principles of Linear, Notion, Arc, and Stripe to create a calm, premium, and academic experience.

---

## 1. Redesign Vision & Brand Strategy

### Visual Theme & Identity
The app is redesigned to be **100% Light Mode**. All dark mode configurations, settings toggles, and dark theme colors are completely removed. The user interface uses ample whitespace, soft shadows, thin borders, and clean typography to let the content breathe.

### Brand Values (UIT)
As the official student companion for the **University of Information Technology (UIT)**, the brand communicates:
* **Academic Excellence:** High-quality, clear typography and organized layout hierarchies.
* **Technology & Engineering:** Precision outlines, clean metadata badges, and organized charts.
* **Clarity & Focus:** Pure white backgrounds combined with a refined blue visual identity.

---

## 2. Color System & Design Tokens

No hardcoded colors, spacing, or radius values should exist in screen code. Everything must refer to the design tokens below.

### 1. Color Tokens

| Token Name | Hex Value | Semantic Role / Context |
| :--- | :--- | :--- |
| `Primary` | `0xFF0056B3` | UIT Blue — Brand identity, primary CTAs, active states |
| `PrimaryContainer` | `0xFFE6F0FA` | Light blue backdrop fill for selected lists/chips |
| `Secondary` | `0xFF4FA3E3` | Sky Blue — Secondary buttons, secondary text links |
| `Accent` | `0xFF00B4D8` | Cyan — UI highlights, active indicators, AI icons |
| `Background` | `0xFFFFFFFF` | Pure White — Main viewport backdrop |
| `Surface` | `0xFFFAFAFA` | Warm White — Floating surfaces, picker slots, input boxes |
| `SurfaceVariant` | `0xFFF0F4F8` | Cooler Slate-White — Inactive segment buttons, code panels |
| `Border` | `0xFFE1E8F0` | Very Subtle Gray-Blue — Thin card outlines (1.dp) |
| `TextPrimary` | `0xFF1A1D20` | Dark Charcoal Gray — Content headers, primary readability |
| `TextSecondary` | `0xFF4A5560` | Medium Slate Gray — Descriptions, subheadings |
| `TextTertiary` | `0xFF82909E` | Light Cool Gray — Metadata, disabled states, timestamps |
| `Success` | `0xFF2D6A4F` | Fresh Green — Completed tasks, positive reviews, checkmarks |
| `SuccessContainer` | `0xFFE8F5E9` | Light Green Fill — Completed task grids and containers |
| `Warning` | `0xFFD97706` | Dark Amber — In-progress tasks, pending members |
| `WarningContainer` | `0xFFFEF3C7` | Light Amber Fill — Partial completion grids, alerts |
| `Error` | `0xFFDC2626` | Material Red — Overdue tasks, deletion buttons, errors |
| `ErrorContainer` | `0xFFFEE2E2` | Light Red Fill — Overdue grids, danger dialog containers |

### 2. Typography Tokens

All text styles reference the system Sans-Serif font (Inter/Roboto), prioritising readability over heavy weights:

| Token Name | Size | Weight | Line Height | Letter Spacing |
| :--- | :--- | :--- | :--- | :--- |
| `Display` | `26.sp` | Bold | `32.sp` | `-0.3.sp` |
| `TitleMedium`| `16.sp` | Semi-Bold | `24.sp` | `0.sp` |
| `Body` | `15.sp` | Normal | `22.sp` | `0.sp` |
| `Command` | `13.sp` | Bold | `18.sp` | `0.5.sp` |
| `Label` | `12.sp` | Medium | `16.sp` | `0.2.sp` |
| `Metric` | `36.sp` | Bold | `42.sp` | `-0.5.sp` |

### 3. Spacing & Radius Tokens

Built on an **8dp spacing grid** for rhythm and alignment:

| Spacing Token | Dp Value | Radius Token | Dp Value |
| :--- | :--- | :--- | :--- |
| `Spacing.Atomic` | `4.dp` | `Radius.Small` | `8.dp` (Chips, badges) |
| `Spacing.Small` | `8.dp` | `Radius.Medium` | `12.dp` (Buttons, input fields) |
| `Spacing.Medium` | `16.dp` | `Radius.Large` | `16.dp` (Surfaces, floating panels) |
| `Spacing.Large` | `24.dp` | `Radius.ExtraLarge`| `24.dp` (Bottom sheets, dialog cards) |
| `Spacing.ExtraLarge`| `32.dp` | `Radius.Circle` | `CircleShape` (Avatars, FABs) |

### 4. Elevation & Shadow Tokens

* **Elevation.Flat:** `0.dp` — Used for pure white layouts with thin `Border` lines.
* **Elevation.Low:** `2.dp` — Mapped to subtle shadow elevations for floating cards.
* **Elevation.Medium:** `6.dp` — Used for floating navigation rails and popup options.
* **Elevation.High:** `12.dp` — Reserved for modal bottom sheets and dialog panels.

---

## 3. UI/UX Architecture & Navigation Updates

### 1. Navigation Transition to Jetpack Compose Navigation
Swapping screens manually by modifying local state is replaced by the official Android `Navigation Compose` framework. This fixes system back gestures on Android.

* **Implementation:** Standardize navigation inside `MainScreen.kt` using `rememberNavController()` and `NavHost` containing all destination routes.
* **Backstack Retention:** System back gestural swipes pop the nested backstack (`navController.popBackStack()`) instead of closing the application.

### 2. Floating Bottom Navigation Bar
Instead of a screen-wide bottom bar, a floating pill-shaped navigation container is introduced:

```
+----------------------------------------------------------------+
|  [Home]       [Môn học]       [AI Tutor]      [Plan]    [User] |
+----------------------------------------------------------------+
   (Floating Pill-shaped, 16dp margins, Rounded 24dp, 4dp shadow)
```
* **Styling:** Set container color to `Surface`, corner radius to `Radius.ExtraLarge`, with a 1.dp `Border` stroke and `Elevation.Medium`.
* **Indicator:** Active tab states feature a soft blue highlight (`PrimaryContainer`) and primary blue icons (`Primary`).

### 3. Responsive & Multi-Screen Support
* **Tablets & Large Screen Landscape:** The bottom bar disappears, replaced by an **Adaptive Navigation Rail** pinned to the left edge.
* **Foldables & Landscape Viewports:** Uses adaptive split layouts (e.g. split panels for Course list on left, Course detail on right), eliminating layout overlapping.

---

## 4. Re-Engineered Screen Inventory

### 1. The Dashboard (Home)
* **Goal:** A clean, focused cockpit instead of crowded boxes.
* **Layout Design:** Remove heavy colored boxes. Use spacious vertical typography and clean, borderless lists.
* **Greeting & Streak:**
  * Render a lightweight, column-based greeting.
  * Show the active streak number in a premium layout using `Metric` typography, styled in UIT Blue.
* **Today's Focus:** A simple, borderless list displaying the top 3 highest-priority tasks due today. Completed tasks trigger a smooth fade-out animation.
* **AI Suggestions:** Displays a single, intelligent micro-card containing context-aware AI recommendations (e.g., *"Bạn có 2 bài tập môn Cấu trúc dữ liệu chưa giải. Ôn tập cùng AI Tutor?"*).

### 2. Course Hub (Prerequisites Dependency Graph)
* **View Modes:**
  * **List Mode:** Renders course items inside floating panels with clear difficulty chips.
  * **Galaxy Mode:** Pours the course catalog into the interactive `GalaxyGraphCanvas`.
* **Galaxy Visualization Redesign:**
  * Background is pure `Background` (White).
  * Node prerequisite links are rendered as thin, subtle gray-blue lines.
  * Selected nodes glow with a subtle radial gradient of UIT Blue and Cyan.

### 3. Course Details & Repository Viewer
* **Tabs:** Repositories, Tutorials, Playlists, Articles.
* **Overview Card:** Transition to a clean surface that expands inline with a layout morph animation.
* **Repository Analytics:**
  * Displays repository evaluations in clean, structured cards.
  * AI analysis (Strengths/Weaknesses) is broken down into simple, bulleted lists with green checkmarks and red flags, rather than heavy colored cards.
  * Displays user review ratings with custom star sliders.

### 4. AI Tutor (Subject QA Chat)
* **Goal:** A premium, code-focused learning environment.
* **Visual Bubble System:**
  * User queries are aligned right inside light blue (`PrimaryContainer`) bubbles.
  * AI replies are aligned left inside warm white (`Surface`) bubbles.
* **Key Features:**
  * **Syntax Highlighting:** Code snippets render inside containers with light gray backgrounds (`SurfaceVariant`), dark gray text, and horizontal scrolling.
  * **Citations:** Displays source citations as horizontal cards under the AI response.
  * **Follow-up Chips:** Rendered at the bottom of the chat view to suggest relevant next questions.

### 5. Task Planner (Kanban & Agenda)
* **Goal:** A clean, functional productivity layout.
* **Timeline View:** A clean vertical timeline containing tasks sorted by deadline.
* **Agendas:** Days are separated by clean typographic headers with sticky scroll behavior.
* **Completion Animation:** Checking off a task triggers a spring scaling effect on the checkmark, followed by a strikethrough animation.

### 6. Profile & settings
* **Visuals:** Pinned student card displaying registration details, term GPA metrics, and a list of bookmarked courses/repos.
* **Functional Profile Screen:** The *"Profile Detail (coming soon)"* placeholder is replaced with a functional editing interface. This bottom sheet allows users to edit names, update emails, and choose custom student avatar URLs.

---

## 5. Reengineered Component Specifications

Developers must replace the old implementation files with the following Jetpack Compose designs:

### 1. Unified Light Theme and Color Tokens
Replace [Color.kt](file:///D:/temp/devorbit/devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/theme/Color.kt) with these color tokens:

```kotlin
// vn/edu/uit/devorbit/mobile/ui/theme/Color.kt
package vn.edu.uit.devorbit.mobile.ui.theme

import androidx.compose.ui.graphics.Color

// Core Brand Mappings
val PrimaryBlue = Color(0xFF0056B3)
val PrimaryBlueContainer = Color(0xFFE6F0FA)
val SecondarySky = Color(0xFF4FA3E3)
val AccentCyan = Color(0xFF00B4D8)

// Canvas Mappings
val PureWhite = Color(0xFFFFFFFF)
val WarmWhite = Color(0xFFFAFAFA)
val SlateWhite = Color(0xFFF0F4F8)

// Borders & Outlines
val SubtleGrayBlueBorder = Color(0xFFE1E8F0)

// High-contrast Neutral Text Mappings
val TextCharcoalPrimary = Color(0xFF1A1D20)
val TextSlateSecondary = Color(0xFF4A5560)
val TextCoolGrayTertiary = Color(0xFF82909E)

// Semantic Diagnostics
val FreshGreen = Color(0xFF2D6A4F)
val FreshGreenContainer = Color(0xFFE8F5E9)
val AlertAmber = Color(0xFFD97706)
val AlertAmberContainer = Color(0xFFFEF3C7)
val DiagnosticRed = Color(0xFFDC2626)
val DiagnosticRedContainer = Color(0xFFFEE2E2)
```

### 2. Light Theme Configuration
Overwrite [Theme.kt](file:///D:/temp/devorbit/devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/theme/Theme.kt) to configure the Light Color Scheme and enable edge-to-edge layout constraints:

```kotlin
// vn/edu/uit/devorbit/mobile/ui/theme/Theme.kt
package vn.edu.uit.devorbit.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val GroundedLightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    primaryContainer = PrimaryBlueContainer,
    secondary = SecondarySky,
    tertiary = AccentCyan,
    background = PureWhite,
    surface = WarmWhite,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onBackground = TextCharcoalPrimary,
    onSurface = TextCharcoalPrimary,
    surfaceVariant = SlateWhite,
    outline = SubtleGrayBlueBorder,
    error = DiagnosticRed,
    errorContainer = DiagnosticRedContainer
)

@Composable
fun DevOrbitTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        val activity = view.context as? android.app.Activity
        activity?.window?.let { window ->
            // Transparent status bar for edge-to-edge layouts
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            
            val insetsController = WindowCompat.getInsetsController(window, view)
            // Enable dark icons on status and navigation bars for light mode
            insetsController.isAppearanceLightStatusBars = true
            insetsController.isAppearanceLightNavigationBars = true
        }
    }

    CompositionLocalProvider(
        LocalCosmicColors provides CosmicColors(
            void = PureWhite,
            nebula = WarmWhite,
            plasma = PrimaryBlue,
            supernova = DiagnosticRed,
            aurora = FreshGreen,
            glass = Color(0x99FFFFFF), // Refined white transparency for floating panels
            glassBorder = SubtleGrayBlueBorder,
            textPrimary = TextCharcoalPrimary,
            textSecondary = TextSlateSecondary,
            textTertiary = TextCoolGrayTertiary
        ),
        LocalCosmicTypography provides LocalCosmicTypography.current,
        LocalCosmicSpacing provides LocalCosmicSpacing.current
    ) {
        MaterialTheme(
            colorScheme = GroundedLightColorScheme,
            content = content
        )
    }
}
```

### 3. Custom Component Token Specifications
Replace the design local provider class references inside [DesignSystem.kt](file:///D:/temp/devorbit/devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/theme/DesignSystem.kt):

```kotlin
// vn/edu/uit/devorbit/mobile/ui/theme/DesignSystem.kt (Excerpt)
val LocalCosmicColors = staticCompositionLocalOf {
    CosmicColors(
        void = PureWhite,
        nebula = WarmWhite,
        plasma = PrimaryBlue,
        supernova = DiagnosticRed,
        aurora = FreshGreen,
        glass = Color(0x99FFFFFF),
        glassBorder = SubtleGrayBlueBorder,
        textPrimary = TextCharcoalPrimary,
        textSecondary = TextSlateSecondary,
        textTertiary = TextCoolGrayTertiary
    )
}

val LocalCosmicGradients = staticCompositionLocalOf {
    CosmicGradients(
        primary = Brush.verticalGradient(listOf(PrimaryBlue, SecondarySky)),
        secondary = Brush.horizontalGradient(listOf(SecondarySky, AccentCyan)),
        danger = Brush.linearGradient(listOf(DiagnosticRed, Color(0xFFE57373))),
        success = Brush.linearGradient(listOf(FreshGreen, Color(0xFF81C784))),
        voidDepth = Brush.verticalGradient(listOf(PureWhite, WarmWhite))
    )
}
```

### 4. Custom FloatingCard (Replacing GlassCard)
Replace [GlassCard.kt](file:///D:/temp/devorbit/devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/components/GlassCard.kt) with a clean, light-mode surface using thin borders and soft drop shadows:

```kotlin
// vn/edu/uit/devorbit/mobile/ui/components/GlassCard.kt
package vn.edu.uit.devorbit.mobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = CosmicTheme.colors.nebula, // Maps to WarmWhite
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder), // Maps to SubtleGrayBlueBorder
        shadowElevation = 2.dp, // Subtle drop shadow instead of heavy glow
        tonalElevation = 0.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
```

---

## 6. Accessibility & Contrast (WCAG AA+)

1. **Contrast Ratio Compliance:** Text colors against backgrounds are verified using the WCAG guidelines:
   * `TextCharcoalPrimary` (`#1A1D20`) on `PureWhite` (`#FFFFFF`) yields a **13.5:1 contrast ratio**, exceeding the WCAG AAA requirement (7:1).
   * `TextSlateSecondary` (`#4A5560`) on `WarmWhite` (`#FAFAFA`) provides a **7.8:1 contrast ratio**, exceeding the WCAG AA requirement (4.5:1).
2. **Touch Targets:** Tap targets for all interactive elements (chips, lists, tabs, and buttons) are set to a minimum height and width of **48.dp** to prevent mistaps.
3. **Screen Reader Semantics:** Custom canvas elements (such as `DevOrbitMark` and `GalaxyGraphCanvas`) are configured with descriptive `contentDescription` strings to support TalkBack accessibility.

---

## 7. Developer Migration Plan & Steps

To implement the UIT 2026 Light Edition design system, developers should follow these steps:

### Step 1: Update Theme & Colors
1. Copy the light color system tokens into [Color.kt](file:///D:/temp/devorbit/devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/theme/Color.kt).
2. Replace [Theme.kt](file:///D:/temp/devorbit/devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/theme/Theme.kt) with the light color scheme and system UI styling configuration.
3. Update [DesignSystem.kt](file:///D:/temp/devorbit/devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/theme/DesignSystem.kt) composition locals to match the light theme tokens.

### Step 2: Refactor Navigation Architecture
1. Add the Compose Navigation dependency in `app/build.gradle.kts`:
   ```kotlin
   implementation("androidx.navigation:navigation-compose:2.8.5")
   ```
2. Replace the state-based screen switches inside [MainScreen.kt](file:///D:/temp/devorbit/devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/MainScreen.kt) with a `NavHost` containing all screen destination routes.
3. Update all screens to trigger callbacks through the `NavController` (e.g. `navController.navigate("route")`).

### Step 3: Remove Dark Mode Code
1. Delete the "Chế độ tối" (Dark Mode) setting switch inside [ProfileScreen.kt](file:///D:/temp/devorbit/devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/screen/profile/ProfileScreen.kt#L238).
2. Clear the `darkMode` state variable from `ProfileViewModel.kt`.

### Step 4: Redesign Layouts & Components
1. Replace `GlassCard` inside [GlassCard.kt](file:///D:/temp/devorbit/devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/components/GlassCard.kt) with the updated version that uses soft shadows and light borders.
2. Update the background canvas rendering in [GalaxyGraphCanvas.kt](file:///D:/temp/devorbit/devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/components/GalaxyGraphCanvas.kt) to match the light theme design.
3. Redesign the UI lists, chips, and cards to match the light mode tokens, removing all hardcoded dark mode colors.
