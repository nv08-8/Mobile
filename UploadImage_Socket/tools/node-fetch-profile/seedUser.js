// seedUser.js
// Usage: node seedUser.js <mongodb_uri> [id] [dbName] [collectionName]
// Example:
//   node seedUser.js "mongodb+srv://user:pw@cluster0.hou92vo.mongodb.net/?appName=Cluster0" 5 appfoods profiles

const { MongoClient } = require('mongodb');

async function seed(uri, id = '5', dbName = 'appfoods', colName = 'profiles') {
  console.log('Seed start. URI:', uri, 'id:', id, 'db:', dbName, 'col:', colName);
  const client = new MongoClient(uri, { serverSelectionTimeoutMS: 10000 });
  try {
    await client.connect();
    console.log('Connected to MongoDB');
    const db = client.db(dbName);
    const col = db.collection(colName);
    const numericId = (!isNaN(Number(id)) ? Number(id) : id);
    // Extended profile fields to match what the app/profile expects
    const doc = {
      id: numericId,
      username: `user${id}`,
      fname: '',
      email: `user${id}@example.com`,
      displayName: `User ${id}`,
      gender: 'Male',
      phone: '',
      // images can be a string URL or array; configure to what your app expects
      images: '',
      // example image URL (optional) - you can change this to a real uploaded image path
      // images: 'http://app.iotstar.vn:8081/appfoods/upload/3layer.png',
      createdAt: new Date(),
      updatedAt: new Date()
    };
    const res = await col.updateOne({ id: numericId }, { $set: doc }, { upsert: true });
    console.log('Upsert result:', res.result || res);
    console.log('Seeded document:', JSON.stringify(doc));
    return true;
  } catch (err) {
    console.error('Error during seeding:', err && err.message ? err.message : err);
    const msg = String(err && err.message || '').toLowerCase();
    if (msg.includes('ssl') || msg.includes('tls') || msg.includes('alert') || msg.includes('certificate')) {
      console.error('\nLikely cause: TLS/SSL handshake failed. Commonly this means your client IP is not whitelisted in MongoDB Atlas (Network Access).');
      console.error('Action: Add your public IP to Atlas Network Access (or allow 0.0.0.0/0 temporarily) and retry.');
    }
    if (msg.includes('authentication') || msg.includes('auth')) {
      console.error('\nAuthentication failed. Check username/password and roles for the user in Atlas.');
    }
    return false;
  } finally {
    try { await client.close(); } catch (e) {}
  }
}

if (require.main === module) {
  const uri = process.argv[2];
  if (!uri) {
    console.error('Usage: node seedUser.js <mongodb_uri> [id] [dbName] [collectionName]');
    process.exit(1);
  }
  const id = process.argv[3] || '5';
  const dbName = process.argv[4] || 'appfoods';
  const colName = process.argv[5] || 'profiles';
  seed(uri, id, dbName, colName).then(ok => {
    if (!ok) process.exit(1);
    process.exit(0);
  });
}

module.exports = { seed };
