const fs = require('fs');
const path = require('path');
function dbg(msg) {
  try { fs.appendFileSync(path.join(__dirname,'server-debug.log'), msg + '\n'); } catch (e) {}
}

dbg('start');
let express, app, port, fetchProfile, MongoClient, ServerApiVersion;
try {
  dbg('before express require');
  console.log('DEBUG: before require express');
  express = require('express');
  dbg('express required');
  console.log('DEBUG: express required');
  app = express();
  dbg('app created');
  console.log('DEBUG: app created');
  port = process.env.PORT || 3000;
  dbg('before fetchProfile require');
  console.log('DEBUG: before require fetchProfile');
  fetchProfile = require('./fetchProfile').fetchProfile;
  dbg('fetchProfile required');
  console.log('DEBUG: fetchProfile required');
  dbg('before mongodb require');
  console.log('DEBUG: before require mongodb');
  ({ MongoClient, ServerApiVersion } = require('mongodb'));
  dbg('mongodb required');
  console.log('DEBUG: mongodb required');
} catch (e) {
  dbg('require failed: ' + (e && e.message ? e.message : String(e)));
  console.error('Top-level require failed in server.js:', e && e.message ? e.message : e);
  console.error(e && e.stack ? e.stack : e);
  // Exit so the error is visible to the caller
  process.exit(1);
}

// Serve uploads statically
const UPLOAD_DIR = path.join(__dirname, 'uploads');
if (!fs.existsSync(UPLOAD_DIR)) fs.mkdirSync(UPLOAD_DIR, { recursive: true });
app.use('/uploads', express.static(UPLOAD_DIR));

// use multer for file uploads
const multer = require('multer');
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, UPLOAD_DIR);
  },
  filename: function (req, file, cb) {
    const unique = Date.now() + '_' + Math.random().toString(36).substring(2,8);
    const safe = file.originalname.replace(/[^a-zA-Z0-9._-]/g, '_');
    cb(null, unique + '_' + safe);
  }
});
const upload = multer({ storage: storage });

dbg('after top-level requires');
const MONGO_URI = process.env.MONGO_URI || 'mongodb+srv://nhuvonguyen2005_db_user:NMqnOXxBvCVStSRj@cluster0.hou92vo.mongodb.net/?appName=Cluster0';
let db = null;
let mongoClient = null;

async function connectDb() {
  if (db) return db;
  try {
    // Newer mongodb driver ignores legacy options; only pass serverApi.
    mongoClient = new MongoClient(MONGO_URI, {
      serverApi: ServerApiVersion && ServerApiVersion.v1 ? ServerApiVersion.v1 : undefined
    });
    await mongoClient.connect();
    // use default database from connection string if available
    const dbName = process.env.MONGO_DB || 'appfoods';
    db = mongoClient.db(dbName);
    console.log('Connected to MongoDB');
    return db;
  } catch (err) {
    console.error('MongoDB connection error:', err.message || err);
    db = null;
    return null;
  }
}

// /fetch handler unchanged
app.get('/fetch', async (req, res) => {
  const id = req.query.id || '5';
  const imagePath = req.query.image || null;

  if (!db) {
    try {
      await connectDb();
    } catch (e) {
      console.error('Error connecting to DB on request:', e.message || e);
    }
  }

  try {
    if (db) {
      try {
        const profilesCol = db.collection('profiles');
         // Find by numeric or string id (seeded docs may have numeric id)
        const q = isNaN(Number(id)) ? { id: String(id) } : { $or: [{ id: Number(id) }, { id: String(id) }] };
        const cached = await profilesCol.findOne(q);
        if (cached) {
          // Return PHP-style JSON so the app can parse like the existing API
          return res.json({ success: true, message: 'Thành công', result: [cached] });
        }
      } catch (e) {
        console.error('Error querying DB:', e.message || e);
      }
    }

    const result = await fetchProfile(id, imagePath);

    if (result && result.profile && db) {
      try {
        const profilesCol = db.collection('profiles');
        const doc = Object.assign({}, result.profile, { id: String(id), _fetchedAt: new Date(), _imagePathLocal: result.imagePath || null });
        await profilesCol.updateOne({ id: String(id) }, { $set: doc }, { upsert: true });
        // Return the newly fetched profile in PHP-style JSON
        return res.json({ success: true, message: 'Thành công', result: [doc] });
      } catch (e) {
        console.error('Error upserting profile to DB:', e.message || e);
      }
    }

    // If we got here, no profile was found/returned: keep PHP behaviour of success with empty result
    return res.json({ success: true, message: 'Thành công', result: [] });
  } catch (err) {
    console.error('Fetch error:', err.message || err);
    // On error, return a PHP-like failure shape
    res.status(500).json({ success: false, message: String(err.message || err), result: [] });
  }
});

// Replace previous raw-body updateimages.php handler with multer-powered one
// Create a reusable handler so we can expose a friendly /api/upload alias
async function uploadHandler(req, res) {
  try {
    // req.body.id should contain the id part
    const id = (req.body && req.body.id) ? req.body.id : null;
    const file = req.file; // may be undefined if client didn't send file

    if (!id && !file) {
      return res.json({ success: true, message: 'Thành công', result: [] });
    }

    if (!db) {
      try { await connectDb(); } catch (e) { console.error('DB connect error in updateimages.php', e); }
    }

    // if file uploaded, build public URL (using server host/port). Use request headers host if available
    let publicUrl = null;
    if (file) {
      const host = req.headers.host || ('localhost:' + port);
      const protocol = req.protocol || 'http';
      // Use /uploads/<filename>
      publicUrl = `${protocol}://${host}/uploads/${encodeURIComponent(file.filename)}`;
    }

    // If we have DB, update profile document: set images to publicUrl (or keep existing if no file)
    if (db) {
      try {
        const profilesCol = db.collection('profiles');
        const q = isNaN(Number(id)) ? { id: String(id) } : { $or: [{ id: Number(id) }, { id: String(id) }] };
        const update = {};
        if (publicUrl) update.images = publicUrl;
        update.updatedAt = new Date();
        const result = await profilesCol.findOneAndUpdate(q, { $set: update }, { upsert: false, returnDocument: 'after' });
        if (result && result.value) {
          return res.json({ success: true, message: 'Thành công', result: [result.value] });
        } else {
          // If not found, optionally upsert using id
          if (publicUrl) {
            const doc = { id: (isNaN(Number(id)) ? String(id) : Number(id)), images: publicUrl, createdAt: new Date(), updatedAt: new Date() };
            await profilesCol.updateOne({ id: doc.id }, { $set: doc }, { upsert: true });
            return res.json({ success: true, message: 'Thành công', result: [doc] });
          }
        }
      } catch (e) {
        console.error('DB update error in updateimages.php:', e);
      }
    }

    // fallback: if no db or not found, attempt to call fetchProfile to get existing info
    try {
      const fetched = await fetchProfile(id, null);
      if (fetched && fetched.profile) {
        return res.json({ success: true, message: 'Thành công', result: [fetched.profile] });
      }
    } catch (e) {
      console.error('fetchProfile fallback error:', e.message || e);
    }

    return res.json({ success: true, message: 'Thành công', result: [] });
  } catch (err) {
    console.error('Unexpected error in multer updateimages.php handler:', err);
    return res.status(500).json({ success: false, message: String(err.message || err), result: [] });
  }
}

// Register both legacy PHP path and new friendly API path
app.post('/updateimages.php', upload.single('images'), uploadHandler);
app.post('/api/upload', upload.single('images'), uploadHandler);

// previous simple listen call replaced with a robust start function that retries ports if they're in use
const http = require('http');

async function startServerWithRetry(startPort, maxAttempts = 5) {
  let portToTry = Number(startPort) || 3000;
  for (let attempt = 0; attempt < maxAttempts; attempt++) {
    try {
      const server = http.createServer(app);
      await new Promise((resolve, reject) => {
        server.once('error', (err) => reject(err));
        server.once('listening', () => resolve());
        server.listen(portToTry);
      });
      console.log(`Server running on http://localhost:${portToTry}`);
      return { server, port: portToTry };
    } catch (err) {
      if (err && err.code === 'EADDRINUSE') {
        console.error(`Port ${portToTry} in use, trying ${portToTry + 1}...`);
        portToTry = portToTry + 1;
        // small backoff before retry
        await new Promise(r => setTimeout(r, 100));
        continue;
      }
      // rethrow other errors
      throw err;
    }
  }
  throw new Error(`Unable to bind server after ${maxAttempts} attempts`);
}

let serverInstance = null;

// Start the server (use env PORT if provided)
startServerWithRetry(port, 10).then(({ server, port: actualPort }) => {
  serverInstance = server;
  // update port variable so other code can reference it
  port = actualPort;
}).catch(err => {
  console.error('Failed to start server:', err && err.message ? err.message : err);
  process.exit(1);
});

process.on('SIGINT', async () => {
  console.log('Shutting down...');
  try {
    if (serverInstance) {
      await new Promise((resolve) => serverInstance.close(resolve));
    }
    if (mongoClient) await mongoClient.close();
  } catch (e) {}
  process.exit();
});
