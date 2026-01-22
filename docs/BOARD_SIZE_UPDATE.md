# Chessboard Size Update

## Issue
The chessboard was too small with a maximum width of 600px, making it difficult to see pieces and play comfortably.

## Solution

### Board Size Increase
- **Previous**: `maxWidth: '600px'`
- **Updated**: `maxWidth: '800px'`
- **Impact**: 33% larger board area (360,000px² → 640,000px²)

### Improved Responsive Sizing

#### Piece Size
Replaced complex calculation with modern CSS `clamp()`:
- **Old**: `fontSize: 'calc(min(12.5vw, 600px) / 8 * 0.7)'`
- **New**: `fontSize: 'clamp(2rem, 8vw, 6rem)'`

**Benefits**:
- Minimum size: 2rem (small screens/mobile)
- Scales with viewport: 8vw (tablets)
- Maximum size: 6rem (desktop/large screens)
- More readable and maintainable code
- Better performance (no calc operations)

#### Coordinate Labels
- **Old**: `fontSize: 'calc(min(12.5vw, 600px) / 8 * 0.12)'`
- **New**: `fontSize: 'clamp(0.6rem, 1.2vw, 1rem)'`

**Benefits**:
- Proportionally sized with board
- Always readable
- Scales smoothly across devices

## Technical Details

### CSS clamp() Function
```css
clamp(MIN, PREFERRED, MAX)
```

The browser automatically selects:
- MIN if PREFERRED < MIN
- PREFERRED if MIN ≤ PREFERRED ≤ MAX
- MAX if PREFERRED > MAX

### Board Layout
```typescript
{
  display: 'flex',
  flexWrap: 'wrap',
  width: '100%',
  maxWidth: '800px',      // Increased from 600px
  margin: '0 auto',       // Center horizontally
  aspectRatio: '1 / 1',   // Perfect square
  border: '2px solid #333',
  boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)',
  boxSizing: 'border-box'
}
```

## Result

✅ **Larger board**: 800px max width (vs 600px)
✅ **Better piece visibility**: Pieces scale from 2rem to 6rem
✅ **Responsive**: Works great on all screen sizes
✅ **Centered layout**: Board auto-centers in container
✅ **Maintained aspect ratio**: Always a perfect square
✅ **Cleaner code**: Using modern CSS instead of complex calculations

## Screen Size Examples

| Screen | Board Width | Piece Size | Label Size |
|--------|-------------|------------|------------|
| Mobile (375px) | 375px | ~3rem | ~0.75rem |
| Tablet (768px) | 768px | ~6rem (max) | ~0.92rem |
| Desktop (1200px) | 800px (max) | 6rem (max) | 1rem (max) |
| Large (1920px) | 800px (max) | 6rem (max) | 1rem (max) |

## Commit
```
commit 744af72
Increase chessboard size from 600px to 800px max
- Update maxWidth from 600px to 800px for larger board display
- Replace complex calc() with clamp() for better responsive sizing
- Piece size: clamp(2rem, 8vw, 6rem) - scales between 2rem and 6rem
- Coordinate labels: clamp(0.6rem, 1.2vw, 1rem)
- Maintain aspect ratio and responsive behavior
- Center board within container for better visual balance
```

