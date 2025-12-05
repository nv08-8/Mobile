const axios = require('axios');
const fs = require('fs');
const path = require('path');
const os = require('os');
const FormData = require('form-data');

// Configuration
const API_URL = 'http://app.iotstar.vn:8081/appfoods/updateimages.php';
const OUTPUT_DIR = path.resolve(__dirname, 'output');

async function fetchProfile(id, providedImagePath) {
  let tmpPath;
  try {
    // We'll try several multipart field names because some servers expect images[], some expect images or file.
    const candidateKeys = ['images', 'images[]', 'file'];
    const tmpDir = os.tmpdir();
    tmpPath = path.join(tmpDir, `tmp_profile_image_${Date.now()}.png`);
    let providedOrTmpPath = null;
    if (providedImagePath && fs.existsSync(providedImagePath)) {
      providedOrTmpPath = providedImagePath;
    } else {
      // create a tiny 1x1 PNG so server receives a file
      const onePixelPngBase64 = 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGNgYAAAAAMAAWgmWQ0AAAAASUVORK5CYII=';
      fs.writeFileSync(tmpPath, Buffer.from(onePixelPngBase64, 'base64'));
      providedOrTmpPath = tmpPath;
    }

    let lastError = null;
    let resp = null;
    // First try: send a real file under various field names
    for (const key of candidateKeys) {
      const form = new FormData();
      form.append('id', id);
      form.append(key, fs.createReadStream(providedOrTmpPath), { filename: path.basename(providedOrTmpPath), contentType: 'image/png' });
      console.log(`Trying multipart key (file): '${key}'`);
      try {
        resp = await axios.post(API_URL, form, { headers: form.getHeaders(), timeout: 20000 });
      } catch (e) {
        lastError = e;
        console.error(`Request failed for key '${key}' (file):`, e.message || e);
        // continue to next key
        continue;
      }

      // print status and headers for debugging
      try { console.log('Response status:', resp.status); } catch {};
      try { console.log('Response headers:', JSON.stringify(resp.headers)); } catch {};
      if (!resp || !resp.data) {
        lastError = new Error('Empty response');
        continue;
      }

      // got a response; break to parse
      lastError = null;
      break;
    }

    if (!resp || !resp.data) {
      // Second try: some implementations send the filename only (not the binary) under 'images'
      const filenameOnly = path.basename(providedOrTmpPath);
      console.log('First pass returned no usable response; trying filename-only variants...');
      for (const key of ['images', 'images[]', 'file']) {
        const form = new FormData();
        form.append('id', id);
        form.append(key, filenameOnly);
        console.log(`Trying multipart key (filename-only): '${key}' => ${filenameOnly}`);
        try {
          resp = await axios.post(API_URL, form, { headers: form.getHeaders(), timeout: 20000 });
        } catch (e) {
          lastError = e;
          console.error(`Request failed for key '${key}' (filename-only):`, e.message || e);
          continue;
        }
        try { console.log('Response status:', resp.status); } catch {};
        try { console.log('Response headers:', JSON.stringify(resp.headers)); } catch {};
        if (resp && resp.data) break;
      }
    }
    if (!resp || !resp.data) {
      throw lastError || new Error('Empty response after trying keys');
    }

    // The server may emit PHP notices (HTML) before the JSON. Normalize: extract the JSON substring starting with {"success"
    let raw = resp.data;
    const rawStr = typeof raw === 'string' ? raw : JSON.stringify(raw);
    console.log('Server response (raw):', rawStr);

    // Try to extract JSON part
    const jsonStart = rawStr.indexOf('{"success"');
    if (jsonStart === -1) {
      // fallback: try first '{'
      const idx = rawStr.indexOf('{');
      if (idx === -1) throw new Error('No JSON in response');
      raw = rawStr.substring(idx);
    } else {
      raw = rawStr.substring(jsonStart);
    }

    let data;
    try {
      data = typeof raw === 'string' ? JSON.parse(raw) : raw;
    } catch (parseErr) {
      console.error('Failed to parse JSON from response:', parseErr.message);
      throw new Error('Invalid JSON response');
    }

    // If the server returned no results, return null profile with raw data instead of throwing
    if (!data.result || !Array.isArray(data.result) || data.result.length === 0) {
      console.log('Server returned empty result array:', JSON.stringify(data));
      return { profile: null, raw: data };
    }

    const profile = data.result[0];
    const imageUrl = profile.images;

    if (!imageUrl) {
      console.log('No image URL found for id', id);
      return { profile };
    }

    // download image
    if (!fs.existsSync(OUTPUT_DIR)) fs.mkdirSync(OUTPUT_DIR, { recursive: true });
    const imageResp = await axios.get(imageUrl, { responseType: 'stream', timeout: 20000 });
    const filename = path.basename(new URL(imageUrl).pathname);
    const outPath = path.join(OUTPUT_DIR, filename);

    const writer = fs.createWriteStream(outPath);
    imageResp.data.pipe(writer);

    await new Promise((resolve, reject) => {
      writer.on('finish', resolve);
      writer.on('error', reject);
    });

    console.log('Image saved to', outPath);
    // cleanup tmp file if we created one
    try { if (tmpPath && fs.existsSync(tmpPath)) fs.unlinkSync(tmpPath); } catch (e) {}
    return { profile, imagePath: outPath };
  } catch (err) {
    console.error('Error fetching profile:', err.message || err);
    if (err.response && err.response.data) {
      console.error('Response body:', err.response.data);
    }
    // ensure tmp file cleaned up on error
    try { if (tmpPath && fs.existsSync(tmpPath)) fs.unlinkSync(tmpPath); } catch (e) {}
    throw err;
  }
}

// Optional MongoDB helper for CLI (--mongo). Lazy-require the driver.
let MongoClient;
try {
  MongoClient = require('mongodb').MongoClient;
} catch (e) {
  MongoClient = null;
}

async function fetchFromMongo(uri, id, dbName = 'appfoods', collectionName = 'profiles') {
  if (!MongoClient) throw new Error('mongodb driver not installed. Run: npm install mongodb');
  const client = new MongoClient(uri, { serverSelectionTimeoutMS: 10000 });
  try {
    await client.connect();
    const db = client.db(dbName);
    const col = db.collection(collectionName);
    // try numeric id, string id, or _id
    const candidates = [];
    if (!isNaN(Number(id))) candidates.push({ id: Number(id) });
    candidates.push({ id: String(id) });
    candidates.push({ _id: id });
    for (const q of candidates) {
      const doc = await col.findOne(q);
      if (doc) return doc;
    }
    return null;
  } finally {
    try { await client.close(); } catch (e) {}
  }
}

// CLI
if (require.main === module) {
  // custom simple argv parsing to support --mongo
  const rawArgs = process.argv.slice(2);
  // find flags
  const mongoIdx = rawArgs.indexOf('--mongo');
  const dbIdx = rawArgs.indexOf('--db');
  const colIdx = rawArgs.indexOf('--col');

  if (mongoIdx !== -1) {
    const uri = rawArgs[mongoIdx + 1];
    // If user passed an ID after the URI, use it: --mongo <uri> <id>
    const idArg = rawArgs[mongoIdx + 2] || (rawArgs[0] && !rawArgs[0].startsWith('--') ? rawArgs[0] : '5');
    const dbName = (dbIdx !== -1) ? rawArgs[dbIdx + 1] : 'appfoods';
    const colName = (colIdx !== -1) ? rawArgs[colIdx + 1] : 'profiles';
    (async () => {
      try {
        console.log('Using Mongo URI:', uri, 'db:', dbName, 'col:', colName, 'id:', idArg);
        const doc = await fetchFromMongo(uri, idArg, dbName, colName);
        if (!doc) {
          console.log('No document found for id', idArg);
          process.exit(0);
        }
        console.log('Found document:', JSON.stringify(doc));
        process.exit(0);
      } catch (err) {
        console.error('Mongo fetch error:', err.message || err);
        process.exit(1);
      }
    })();
  } else {
    const id = process.argv[2] || '5';
    const img = process.argv[3];
    fetchProfile(id, img).then(res => {
      if (res && res.profile && res.profile.username) console.log('Done:', res.profile.username);
      else if (res && res.raw) console.log('Done: no profile, server raw:', JSON.stringify(res.raw));
      else console.log('Done: no profile');
    }).catch(err => process.exit(1));
  }
}

module.exports = { fetchProfile, fetchFromMongo };
