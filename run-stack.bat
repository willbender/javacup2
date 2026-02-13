@echo off
REM JavaCup Docker Stack Launcher for Windows
REM This script allows you to run the JavaCup stack with Docker or Podman

setlocal enabledelayedexpansion

REM Parse command line arguments
set "RUNTIME="
set "COMMAND="

:parse_args
if "%~1"=="" goto :end_parse_args
if /i "%~1"=="-r" (
    set "RUNTIME=%~2"
    shift
    shift
    goto :parse_args
)
if /i "%~1"=="--runtime" (
    set "RUNTIME=%~2"
    shift
    shift
    goto :parse_args
)
if /i "%~1"=="-c" (
    set "COMMAND=%~2"
    shift
    shift
    goto :parse_args
)
if /i "%~1"=="--command" (
    set "COMMAND=%~2"
    shift
    shift
    goto :parse_args
)
if /i "%~1"=="-h" goto :show_usage
if /i "%~1"=="--help" goto :show_usage
echo [ERROR] Unknown option: %~1
goto :show_usage

:end_parse_args

REM Detect or validate container runtime
if "%RUNTIME%"=="" (
    where docker >nul 2>&1
    if !errorlevel! equ 0 (
        set "RUNTIME=docker"
        echo [INFO] Detected container runtime: docker
    ) else (
        where podman >nul 2>&1
        if !errorlevel! equ 0 (
            set "RUNTIME=podman"
            echo [INFO] Detected container runtime: podman
        ) else (
            echo [ERROR] No container runtime found. Please install Docker or Podman.
            exit /b 1
        )
    )
) else (
    where %RUNTIME% >nul 2>&1
    if !errorlevel! neq 0 (
        echo [ERROR] Specified runtime '%RUNTIME%' not found. Please install it first.
        exit /b 1
    )
)

REM Set compose command based on runtime
if /i "%RUNTIME%"=="docker" (
    where docker-compose >nul 2>&1
    if !errorlevel! equ 0 (
        set "COMPOSE_CMD=docker-compose"
    ) else (
        docker compose version >nul 2>&1
        if !errorlevel! equ 0 (
            set "COMPOSE_CMD=docker compose"
        ) else (
            echo [ERROR] Docker Compose not found. Please install it.
            exit /b 1
        )
    )
) else if /i "%RUNTIME%"=="podman" (
    where podman-compose >nul 2>&1
    if !errorlevel! equ 0 (
        set "COMPOSE_CMD=podman-compose"
    ) else (
        echo [ERROR] Podman Compose not found. Please install it: pip install podman-compose
        exit /b 1
    )
)

echo [INFO] Using: !COMPOSE_CMD!

REM If command not specified, show interactive menu
if "%COMMAND%"=="" (
    echo.
    echo JavaCup Docker Stack Manager
    echo ==============================
    echo 1^) Start stack ^(up^)
    echo 2^) Stop stack ^(down^)
    echo 3^) Restart stack
    echo 4^) View logs
    echo 5^) Show running containers ^(ps^)
    echo 6^) Rebuild images
    echo 7^) Exit
    echo.
    set /p "choice=Select option [1-7]: "
    
    if "!choice!"=="1" set "COMMAND=up"
    if "!choice!"=="2" set "COMMAND=down"
    if "!choice!"=="3" set "COMMAND=restart"
    if "!choice!"=="4" set "COMMAND=logs"
    if "!choice!"=="5" set "COMMAND=ps"
    if "!choice!"=="6" set "COMMAND=build"
    if "!choice!"=="7" exit /b 0
    
    if "!COMMAND!"=="" (
        echo [ERROR] Invalid option
        exit /b 1
    )
)

REM Execute the command
if /i "%COMMAND%"=="up" (
    echo [INFO] Starting JavaCup stack...
    call !COMPOSE_CMD! up -d
    if !errorlevel! equ 0 (
        echo [INFO] Stack started successfully!
        echo [INFO] Frontend: http://localhost:3000
        echo [INFO] Backend:  http://localhost:8080
    )
) else if /i "%COMMAND%"=="down" (
    echo [INFO] Stopping JavaCup stack...
    call !COMPOSE_CMD! down
    if !errorlevel! equ 0 echo [INFO] Stack stopped successfully!
) else if /i "%COMMAND%"=="restart" (
    echo [INFO] Restarting JavaCup stack...
    call !COMPOSE_CMD! restart
    if !errorlevel! equ 0 echo [INFO] Stack restarted successfully!
) else if /i "%COMMAND%"=="logs" (
    echo [INFO] Showing logs ^(Ctrl+C to exit^)...
    call !COMPOSE_CMD! logs -f
) else if /i "%COMMAND%"=="ps" (
    echo [INFO] Running containers:
    call !COMPOSE_CMD! ps
) else if /i "%COMMAND%"=="build" (
    echo [INFO] Rebuilding images...
    call !COMPOSE_CMD! build --no-cache
    if !errorlevel! equ 0 echo [INFO] Images rebuilt successfully!
) else (
    echo [ERROR] Unknown command: %COMMAND%
    goto :show_usage
)

goto :eof

:show_usage
echo Usage: %~nx0 [OPTIONS]
echo.
echo Options:
echo     -r, --runtime RUNTIME    Container runtime to use ^(docker or podman^)
echo     -c, --command COMMAND    Command to execute ^(up, down, restart, logs, ps, build^)
echo     -h, --help              Show this help message
echo.
echo Examples:
echo     %~nx0                      # Interactive mode
echo     %~nx0 --runtime docker --command up
echo     %~nx0 -r podman -c down
echo     %~nx0 --command logs
echo.
exit /b 1
