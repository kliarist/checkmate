# CheckMate Color Scheme Reference

## Consistent Color Palette

All menu pages now use a consistent color scheme defined in `frontend/src/styles/menuStyles.ts`.

### Background Colors
- **Main Background**: `#242424` - Dark gray background for all pages
- **Card Background**: `#2d2d2d` - Slightly lighter gray for cards/panels
- **Input/Info Box Background**: `#3d3d3d` - Medium gray for inputs and info boxes
- **Button Background (Secondary)**: `#3a3a3a` - Gray for secondary buttons

### Text Colors
- **Primary Text**: `#e0e0e0` - Light gray for main text
- **Secondary Text**: `#999` - Medium gray for subtitles and secondary text
- **Muted Text**: `#666` - Darker gray for dividers and muted text
- **White Text**: `#fff` - Pure white for button text

### Button Colors

#### Primary Actions
- **Computer Game (Green)**: `#4CAF50` - Green for "Play vs Computer"
- **Private Game (Blue)**: `#2196F3` - Blue for "Play with Friend"
- **Ranked Game (Orange)**: `#FF9800` - Orange for "Ranked Game"
- **Accent/Sign In (Chess Brown)**: `#b58863` - Chess piece brown for sign in and special actions

#### Secondary Actions
- **Secondary Button**: `#3a3a3a` - Gray for sign up and secondary actions
- **Guest/Anonymous**: `transparent` with `#3a3a3a` border - Transparent for guest actions
- **Disabled**: `#666` - Gray for disabled buttons

#### Interactive States
- **Selected State**: `#4CAF50` with `#66BB6A` border - Green for selected options
- **Hover State**: `#4a4a4a` - Slightly lighter gray on hover
- **Border**: `#4a4a4a` or `#555` - Gray borders for inputs and buttons

### Status Colors
- **Error**: `#ff6b6b` - Red for error messages and alerts
- **Success**: `#4CAF50` - Green for success states

## Color Usage by Component

### Landing Page (GuestLandingPage)
- Sign In button: `#b58863` (accent)
- Sign Up button: `#3a3a3a` (secondary)
- Computer button: `#4CAF50` (green)
- Private button: `#2196F3` (blue)
- Ranked button: `#FF9800` (orange)
- Guest button: `transparent` with border

### Authentication Pages (Login/Registration)
- Back button: `#3a3a3a`
- Error box: `#ff6b6b`
- Links: `#b58863` (accent)

### Game Mode Pages (Computer/Private/Ranked)
- Back button: `#3a3a3a`
- Primary action: `#4CAF50` (green)
- Secondary action: `#2196F3` (blue)
- Accent action: `#b58863` (chess brown)
- Disabled: `#666`

### Game Board Pages
- Background: `#242424`
- Info panels: `#3d3d3d`
- Text: `#e0e0e0`

## Consistency Rules

1. All backgrounds use shades of gray (#242424, #2d2d2d, #3d3d3d, #3a3a3a)
2. All text uses light gray (#e0e0e0, #999, #666) or white (#fff)
3. Game mode buttons use distinct colors (green, blue, orange)
4. Accent actions use chess brown (#b58863)
5. Errors always use red (#ff6b6b)
6. Disabled states always use gray (#666)
7. Borders use consistent gray shades (#4a4a4a, #555)

## Files Using Consistent Colors

✅ `frontend/src/styles/menuStyles.ts` - Central color definitions
✅ `frontend/src/pages/GuestLandingPage.tsx` - Uses menuStyles
✅ `frontend/src/pages/LoginPage.tsx` - Uses menuStyles
✅ `frontend/src/pages/RegistrationPage.tsx` - Uses menuStyles
✅ `frontend/src/pages/ComputerGamePage.tsx` - Uses menuStyles
✅ `frontend/src/pages/PrivateGamePage.tsx` - Uses menuStyles
✅ `frontend/src/pages/RankedGamePage.tsx` - Uses menuStyles

## Testing Checklist

- [ ] All menu pages have #242424 background
- [ ] All cards have #2d2d2d background
- [ ] All buttons use defined colors from menuStyles
- [ ] Error messages use #ff6b6b
- [ ] Text colors are consistent (#e0e0e0, #999, #666)
- [ ] No inline color definitions outside of menuStyles
- [ ] Game mode buttons have distinct colors (green, blue, orange)
- [ ] Hover states work correctly
