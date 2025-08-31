# Spring AI MCP Server for Lead Creation

This is a Spring AI MCP (Model Context Protocol) Server that provides a tool for creating leads in Kylas CRM.

## Features

- **Lead Creation Tool**: Creates leads in Kylas CRM with firstName and lastName
- **MCP Protocol**: Implements Model Context Protocol for AI integration
- **OAuth2 Ready**: Configured for OAuth2 security (optional)
- **Docker Support**: Includes Dockerfile for containerization

## Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use included Maven wrapper)
- Kylas API key
- **No database required!** (completely stateless)

## Configuration

### Environment Variables

Set the following environment variables:

```bash
export KYLAS_API_KEY=your-kylas-api-key-here
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/mcpdb
export SPRING_DATASOURCE_USERNAME=mcpuser
export SPRING_DATASOURCE_PASSWORD=mcppass
```

### Application Properties

Key configuration in `src/main/resources/application.properties`:

- `kylas.api.url`: Kylas API endpoint (default: https://api.kylas.io/v1/leads)
- `kylas.api.key`: Your Kylas API key
- Database connection settings

## Running Locally (Ultra-Simplified)

### 🚀 One-Command Start

**No database, no OAuth2, no complexity!**

```bash
# Just run it with your Kylas API key
KYLAS_API_KEY=your-key ./mvnw spring-boot:run
```

**That's it!** The application starts in under 2 seconds and is ready to use.

### ✅ What You Get

- ⚡ **Fast startup**: Under 2 seconds
- 🗄️ **No database**: Completely stateless
- 🔓 **No authentication**: All endpoints open for testing
- 🛠️ **Full MCP protocol**: Ready for AI integration
- 🔗 **Kylas integration**: Real API calls to create leads

### 3. Test the Application

Check health endpoint:
```bash
curl http://localhost:8080/health
```

## MCP Tool Usage

The server exposes one MCP tool:

### create_lead

Creates a new lead in Kylas CRM.

**Parameters:**
- `firstName` (string, required): First name of the lead
- `lastName` (string, required): Last name of the lead

**Example MCP Tool Call:**
```json
{
  "tool": "create_lead",
  "arguments": {
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

## Docker Deployment

### Build Docker Image

```bash
docker build -t mcp-server .
```

### Run with Docker

```bash
docker run -p 8080:8080 \
  -e KYLAS_API_KEY=your-key \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/mcpdb \
  mcp-server
```

## Free Hosting Deployment Options

### 1. Railway (Recommended)

1. Create account at [Railway.app](https://railway.app)
2. Connect your GitHub repository
3. Add environment variables in Railway dashboard:
   - `KYLAS_API_KEY`
   - `SPRING_DATASOURCE_URL` (Railway provides PostgreSQL)
4. Deploy automatically from GitHub

### 2. Render

1. Create account at [Render.com](https://render.com)
2. Create new Web Service from GitHub repo
3. Set build command: `./mvnw clean package -DskipTests`
4. Set start command: `java -jar target/*.jar`
5. Add environment variables in Render dashboard

### 3. Heroku

1. Install Heroku CLI
2. Create Heroku app:
   ```bash
   heroku create your-mcp-server
   heroku addons:create heroku-postgresql:mini
   heroku config:set KYLAS_API_KEY=your-key
   git push heroku main
   ```

### 4. Google Cloud Run

1. Build and push to Google Container Registry:
   ```bash
   gcloud builds submit --tag gcr.io/PROJECT-ID/mcp-server
   ```
2. Deploy to Cloud Run:
   ```bash
   gcloud run deploy --image gcr.io/PROJECT-ID/mcp-server --platform managed
   ```

## Testing the Deployment

Once deployed, test your MCP server:

1. **Health Check:**
   ```bash
   curl https://your-app-url/health
   ```

2. **MCP Endpoint:**
   ```bash
   curl https://your-app-url/mcp
   ```

## Environment Variables

**Only one required:**

```bash
KYLAS_API_KEY=your-actual-kylas-api-key
```

**Optional (for custom configuration):**

```bash
SERVER_PORT=8080                    # Change port if needed
KYLAS_API_URL=https://api.kylas.io/v1/leads  # Custom Kylas endpoint
```

## Security Notes

- **No authentication required** for testing
- OAuth2 dependencies are disabled for simplicity
- Use HTTPS in production
- Keep your Kylas API key secure
- For production, consider re-enabling authentication

## Troubleshooting

1. **Kylas API Issues**: Verify your API key and network connectivity
2. **Port Conflicts**: Change `SERVER_PORT` environment variable if needed
3. **Java Version**: Ensure Java 17+ is installed
4. **Network Issues**: Check firewall settings for outbound HTTPS connections

## API Documentation

- Health: `GET /health`
- Root: `GET /`
- MCP Endpoint: `GET /mcp` (Server-Sent Events)
