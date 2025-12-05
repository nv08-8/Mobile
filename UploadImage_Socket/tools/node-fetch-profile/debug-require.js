console.log('cwd', process.cwd());
try {
  const express = require('express');
  console.log('express required');
  try {
    const fp = require('./fetchProfile');
    console.log('fetchProfile required; exports keys:', Object.keys(fp));
  } catch (e) {
    console.error('require ./fetchProfile failed:', e && e.stack ? e.stack : e);
  }
  try {
    const mongodb = require('mongodb');
    console.log('mongodb required; keys:', Object.keys(mongodb).slice(0,10));
  } catch (e) {
    console.error('require mongodb failed:', e && e.stack ? e.stack : e);
  }
} catch (e) {
  console.error('Top-level require failed:', e && e.stack ? e.stack : e);
}

