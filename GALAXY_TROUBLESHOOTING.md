# GalaxyCurl.http Troubleshooting Guide

## Common Issues and Solutions

### 404 Error (Endpoint Not Found)

**Possible Causes:**
1. Application is not running
2. Application is running on a different port
3. Endpoint URL is incorrect

**Solutions:**
1. Check if application is running: `ps aux | grep SbgbApplication`
2. Verify port: Check `application.yml` for `server.port` (default: 8080)
3. Test basic connectivity: `nc -z localhost 8080`
4. Test GET endpoint: `wget --spider http://localhost:8080/images`

### 400 Error (Bad Request)

**Possible Causes:**
1. Missing required fields in JSON
2. Invalid field values (out of range)
3. Invalid color format (must be #RRGGBB)
4. Database connection issues

**Required Fields Checklist:**

#### ImageRequestCmd (Top Level)
- `sizeCmd` (required, not null)
- `colorCmd` (required, not null)
- `name` (optional but recommended)
- `description` (optional)
- `forceUpdate` (optional, default: false)

#### SizeCmd
- `height` (required, min: 100, max: 4000)
- `width` (required, min: 100, max: 4000)
- `seed` (required, min: 1)
- `octaves` (optional, default: 1, min: 1, max: 10)
- `persistence` (optional, default: 0.5, min: 0.0, max: 1.0)
- `lacunarity` (optional, default: 2.0, min: 1.0)
- `scale` (optional, default: 100.0, min: 1.0, max: 1000.0)

#### ColorCmd
- `back` (required, format: #RRGGBB)
- `middle` (required, format: #RRGGBB)
- `fore` (required, format: #RRGGBB)
- `backThreshold` (required, min: 0.1)
- `middleThreshold` (required, min: 0.1)
- `interpolationType` (optional, default: "LINEAR")

#### LayerCmd (when useMultiLayer = true)
- `octaves` (required, min: 1, max: 10)
- `persistence` (required, min: 0.0, max: 1.0)
- `lacunarity` (required, min: 1.0)
- `scale` (required, min: 1.0, max: 1000.0)
- `opacity` (required, min: 0.0, max: 1.0)
- `blendMode` (required: NORMAL, MULTIPLY, SCREEN, OVERLAY, or ADD)

## Database Configuration

The application requires PostgreSQL database connection. Ensure these environment variables are set:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/db-sbgb
export SPRING_DATASOURCE_USERNAME=user_sbgb
export SPRING_DATASOURCE_PASSWORD=pw_sbgb
```

Or create a `.env` file in the project root:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/db-sbgb
SPRING_DATASOURCE_USERNAME=user_sbgb
SPRING_DATASOURCE_PASSWORD=pw_sbgb
```

## Testing the API

### 1. Test Basic Connectivity

```bash
# Check if port is open
nc -z localhost 8080

# Test GET endpoint
wget --spider http://localhost:8080/images
```

### 2. Test with Minimal POST Request

Create `test_minimal.http`:

```http
POST http://localhost:8080/images/create
Content-Type: application/json

{
  "name": "Test",
  "description": "Test",
  "forceUpdate": false,
  "sizeCmd": {
    "height": 1000,
    "width": 1000,
    "seed": 123,
    "octaves": 1,
    "persistence": 0.5,
    "lacunarity": 2.0,
    "scale": 100.0,
    "preset": "CUSTOM",
    "useMultiLayer": false,
    "noiseType": "FBM"
  },
  "colorCmd": {
    "back": "#000000",
    "middle": "#555555",
    "fore": "#FFFFFF",
    "backThreshold": 0.3,
    "middleThreshold": 0.7,
    "interpolationType": "LINEAR"
  }
}
```

### 3. Test with Galaxy Example

Create `test_galaxy.http`:

```http
POST http://localhost:8080/images/create
Content-Type: application/json

{
  "name": "Test Galaxy",
  "description": "Test galaxy pattern",
  "forceUpdate": false,
  "sizeCmd": {
    "height": 2000,
    "width": 2000,
    "seed": 42,
    "octaves": 1,
    "persistence": 0.5,
    "lacunarity": 2.0,
    "scale": 100.0,
    "preset": "CUSTOM",
    "useMultiLayer": true,
    "noiseType": "FBM",
    "layers": [
      {
        "name": "background",
        "enabled": true,
        "octaves": 2,
        "persistence": 0.3,
        "lacunarity": 2.0,
        "scale": 250.0,
        "opacity": 1.0,
        "blendMode": "NORMAL",
        "noiseType": "FBM",
        "seedOffset": 0
      },
      {
        "name": "galaxy_core",
        "enabled": true,
        "octaves": 3,
        "persistence": 0.7,
        "lacunarity": 2.1,
        "scale": 150.0,
        "opacity": 0.8,
        "blendMode": "ADD",
        "noiseType": "FBM",
        "seedOffset": 1000
      }
    ]
  },
  "colorCmd": {
    "back": "#000000",
    "middle": "#1a004a",
    "fore": "#8a2be2",
    "backThreshold": 0.25,
    "middleThreshold": 0.75,
    "interpolationType": "SMOOTHSTEP"
  }
}
```

## Common Fixes

### Fix 1: Ensure All Required Fields Are Present

Check that every request includes:
- `sizeCmd.height`, `sizeCmd.width`, `sizeCmd.seed`
- `colorCmd.back`, `colorCmd.middle`, `colorCmd.fore`, `colorCmd.backThreshold`, `colorCmd.middleThreshold`
- For layers: all numeric fields with valid ranges

### Fix 2: Validate Color Formats

Colors must be in format `#RRGGBB` (6 hex digits):
- ✅ Valid: `#000000`, `#FFFFFF`, `#1a004a`
- ❌ Invalid: `000000`, `#000`, `black`, `#GGGGGG`

### Fix 3: Check Numeric Ranges

Ensure all numeric values are within valid ranges:
- `height`, `width`: 100-4000
- `seed`: ≥1
- `octaves`: 1-10
- `persistence`: 0.0-1.0
- `lacunarity`: ≥1.0
- `scale`: 1.0-1000.0
- `opacity`: 0.0-1.0
- `backThreshold`, `middleThreshold`: ≥0.1

### Fix 4: Database Connection

Ensure PostgreSQL is running and credentials are correct:

```bash
# Check if database container is running
docker ps | grep db_sbgb

# Test database connection
docker exec spacebackgroundbuilder-db_sbgb-1 psql -U user_sbgb -d db-sbgb -c "SELECT 1;"

# Check application logs for database errors
# (Look for Hibernate/JDBC connection errors)
```

### Fix 5: Restart Application

If configuration changes were made:

```bash
# Kill existing application
pkill -f "SbgbApplication" || echo "No application found"

# Restart with proper environment
cd /home/daniel/dev/SpaceBackGroundBuilder
source .env  # Load environment variables
./mvnw spring-boot:run -pl sbgb-configuration,sbgb-exposition,sbgb-application
```

## Debugging Tips

1. **Check Application Logs**: Look for startup errors or database connection issues
2. **Test with Swagger UI**: Access `http://localhost:8080/swagger-ui.html` to test endpoints interactively
3. **Validate JSON**: Use a JSON validator to check request formats
4. **Start Simple**: Test with minimal requests first, then add complexity
5. **Check Network**: Ensure no firewall is blocking localhost:8080

## Working Examples

See `test_galaxy_fixed.http` for corrected galaxy examples that include all required fields with valid values.