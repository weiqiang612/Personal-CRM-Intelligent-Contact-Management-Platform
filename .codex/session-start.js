const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const APP_PORT = 8080;

try {
  let isRunning = false;
  let pid = '';
  if (process.platform === 'win32') {
    try {
      const output = execSync(`netstat -ano | findstr :${APP_PORT}`, { stdio: ['pipe', 'pipe', 'ignore'] }).toString();
      for (const line of output.split('\n')) {
        if (line.includes('LISTENING')) {
          const parts = line.trim().split(/\s+/);
          pid = parts[parts.length - 1];
          isRunning = true;
          break;
        }
      }
    } catch {}
  } else {
    try {
      pid = execSync(`lsof -t -i:${APP_PORT}`, { stdio: ['pipe', 'pipe', 'ignore'] }).toString().trim();
      if (pid) isRunning = true;
    } catch {}
  }

  if (isRunning) {
    console.log(`✓ Dev server running on port ${APP_PORT} (PID ${pid})`);
  } else {
    console.log(`⚠️  Dev server is NOT running on port ${APP_PORT}.`);
    console.log(`   Windows: pwsh -ExecutionPolicy Bypass -File .\\init.ps1`);
    console.log(`   macOS/Linux/WSL/Git Bash: bash init.sh`);
  }
} catch {}

try {
  const branch = execSync('git branch --show-current', { stdio: ['pipe', 'pipe', 'ignore'] }).toString().trim();
  console.log('\n── Branch & status ────────────────────────────────');
  console.log(`Branch: ${branch || 'unknown'}`);
  const status = execSync('git status --short', { stdio: ['pipe', 'pipe', 'ignore'] }).toString().trim();
  if (status) console.log(status.split('\n').slice(0, 10).join('\n'));
} catch {}

try {
  console.log('\n── Recent commits ──────────────────────────────────');
  console.log(execSync('git log -n 3 --oneline', { stdio: ['pipe', 'pipe', 'ignore'] }).toString().trim());
} catch {}

try {
  console.log('\n── Active task ─────────────────────────────────────');
  const planPath = path.join(process.cwd(), 'docs', '4-tasks', 'CURRENT_PLAN.md');
  if (fs.existsSync(planPath)) {
    const lines = fs.readFileSync(planPath, 'utf8').split('\n');
    let found = false;
    let printed = 0;
    for (const line of lines) {
      if (line.startsWith('## Active feature')) found = true;
      if (found) {
        console.log(line);
        printed += 1;
        if (printed > 10) break;
      }
    }
  } else {
    console.log('No CURRENT_PLAN.md found - run /new-task to create your first task.');
  }
} catch {}

console.log('');
