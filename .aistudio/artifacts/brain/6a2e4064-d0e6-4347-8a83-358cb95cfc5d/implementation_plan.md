# Nothing News — Distinct Dual-Design Engine & 3-Source Fact Verification

Nothing News features a complete bifurcation between two distinct design philosophies: **Nothing OS (Industrial Monochrome)** and **Material 3 / Material You (Google Expressive)**. When toggling between the two, the app transforms its typography, geometry, color palette, component behaviors, pull-to-refresh mechanics, and image rendering so each mode feels authentic to its design philosophy.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following revisions have been incorporated to make Material 3 / Material You completely distinct from Nothing OS:
> - **Nothing OS Mode (Industrial & Stark)**:
>   - *Colors*: Pure monochrome (`#0D0D0D` dark / `#F5F5F5` light), `#1A1A1A` surfaces, 1px `#2E2E2E` borders, `#D71921` Signal Red accent.
>   - *Atmosphere*: Ambient dot-field canvas grid underlay.
>   - *Geometry*: Sharp corners (0–4px max), flat fills, zero shadows.
>   - *Typography*: Space Mono dot-matrix headers, ALL CAPS tracked metadata, Space Grotesk body.
>   - *Components*: Mechanical square-knob toggle, 12-block segmented tick progress bar, Nothing dot-matrix pull-to-refresh.
>   - *Imagery*: High-contrast black & white photos with custom dot-matrix dither overlay.
> - **Material 3 / Material You Mode (Google Expressive & Fluid)**:
>   - *Colors*: Dynamic system palette extracted from the user's wallpaper (`dynamicLightColorScheme` / `dynamicDarkColorScheme` on Android 12+), utilizing primary/secondary/tertiary tonal containers.
>   - *Atmosphere*: Clean, smooth tonal surfaces without any dot-field underlay.
>   - *Geometry*: Organic rounded curves (16dp–28dp pill corners), soft tonal elevation, no harsh 1px borders.
>   - *Typography*: Google modern sans typography, title-case headers, natural proportional font sizing.
>   - *Components*: Official Google M3 Switch (smooth sliding circular thumb), continuous rounded progress indicator, standard Google M3 pull-to-refresh indicator, rounded pill chips.
>   - *Imagery*: Full natural color photography with smooth 16dp rounded corners and subtle elevation.
> - **Fact-Checking Engine (Active across both themes)**:
>   - Cross-references 3+ independent sources for every article.
>   - Credibility ratings, publication timestamps, and direct corroborating excerpts.
>   - Prominent Signal Red inline callouts for any conflicting or unsubstantiated claims.
> - **Streamlined UI**: Glyph Matrix card removed; easter egg **"Made By Demon"** accessible via version badge triple-tap.

---

## 1. Overview & Core Concept

- **What It Does**: Nothing News streams curated, verified wire dispatches with 3-source cross-checking and discrepancy auditing. Users have absolute aesthetic freedom: they can experience the pure industrial Nothing OS aesthetic or switch to Google's Material You experience where every component, surface, and font adapts organically to their device's wallpaper and Google design guidelines.
- **Key Value**: 100% verified, bias-free journalism with two radically different, authentic design systems under one roof.

---

## 2. User Experience & Visual Design

### Comparison Matrix

| Visual / UI Attribute | Nothing OS Mode (Default) | Material 3 / Material You Mode |
| :--- | :--- | :--- |
| **Color System** | Stark Monochrome + `#D71921` Signal Red | Dynamic wallpaper tones (Android 12+) or M3 Pastel |
| **Background** | Sparse Canvas Dot-Field Matrix | Clean solid surface with tonal depth |
| **Card Shape** | Sharp 0–4px corners, 1px `#2E2E2E` border | Smooth 20–24px rounded corners, tonal elevation |
| **Typography** | Space Mono (Dot/Mono) + Space Grotesk | Standard Google Sans / Clean System Sans |
| **Case Styling** | ALL CAPS with letter-spacing for metadata | Sentence / Title case, natural kerning |
| **Switches** | Mechanical rectangular track & sharp knob | Google M3 pill switch with smooth circular thumb |
| **Progress Indicator**| 12-segment discrete tick blocks | Smooth continuous rounded LinearProgressIndicator |
| **Pull-to-Refresh** | Industrial floating pill with LED dot spinner | Google M3 dynamic color circular progress indicator |
| **Article Imagery** | High-contrast B&W with dot-matrix dither | Rich natural color photography, 16dp rounded |
| **Filter Chips** | Sharp rectangular bordered boxes | Soft rounded pill-shaped M3 FilterChips |

### Key User Flows
1. **Curated Feed**:
   - In Nothing mode: Dot field, sharp cards, dithered wire photos, monospace ticker, mechanical pull-to-refresh.
   - In Material You mode: Wallpaper-tinted cards, soft shadows, rounded photos, Google M3 pull-to-refresh.
   - Palestine & Gaza dedicated tab with verified humanitarian reports from UN OCHA, UNRWA, WHO, and ICRC.
2. **Reading View & Inline Discrepancy Callouts**:
   - Both modes feature prominent inline claim callouts highlighting any unsubstantiated rumors or conflicting data across sources with clear source attribution.
3. **Tri-Source Verification Registry**:
   - Details 3 independent primary sources with credibility score, timestamps, and quotes.
4. **Theme Switcher in Settings**:
   - Instant live transition between Nothing OS and Material You styles, with dark/light mode toggle.
5. **Easter Egg**:
   - Triple-tap on version badge in settings reveals the animated **"Made By Demon"** overlay.

---

## 3. Key Product Decisions & Trade-Offs

- **Polymorphic UI Components via `LocalDesignStyle`**:
  - *Chosen Approach*: Component wrappers (`NothingCard`, `NothingSwitch`, `NewsProgressBar`, `NewsPullToRefresh`) branch on `LocalDesignStyle.current` to render either the Nothing industrial specification or standard Google Material 3 components (`CardDefaults.cardColors()`, `Switch`, `LinearProgressIndicator`).
  - *Why*: Ensures code reusability while delivering two completely distinct design languages.
- **3-Source Data Model with Offline Persistence**:
  - *Chosen Approach*: First-class Kotlin models (`VerificationSource`, `DiscrepancyClaim`) stored in Room SQLite DB, ensuring 100% offline availability of full multi-source fact audits and dithered/color images.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                              MainActivity                              │
│         Scaffold, Theme Management & Screen Navigation                 │
└────────────────────────────────────┬───────────────────────────────────┘
                                     │
         ┌───────────────────────────┴───────────────────────────┐
         ▼                                                       ▼
┌──────────────────────────────────┐   ┌──────────────────────────────────┐
│      Nothing OS Branch           │   │      Material You Branch         │
│ - Dot-field canvas               │   │ - Dynamic wallpaper colorScheme  │
│ - Space Mono / Space Grotesk     │   │ - Google Sans / System type      │
│ - 2dp sharp borders              │   │ - 24dp rounded tonal cards       │
│ - Segmented ticks & mech switch  │   │ - Smooth M3 switch & progress    │
│ - Dithered monochrome images     │   │ - Rich full-color images         │
└────────────────┬─────────────────┘   └────────────────┬─────────────────┘
                 │                                      │
                 └───────────────────┬──────────────────┘
                                     │
                                     ▼
┌────────────────────────────────────────────────────────────────────────┐
│                     NewsViewModel & NewsRepository                     │
│  - 3+ independent sources per article with credibility ratings & quotes│
│  - Inline discrepancy models with consensus resolution                 │
│  - Room database offline cache & reading progress tracking             │
└────────────────────────────────────────────────────────────────────────┘
```
