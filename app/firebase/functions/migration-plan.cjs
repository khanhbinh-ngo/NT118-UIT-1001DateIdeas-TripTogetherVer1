function buildMigration(data, now = Date.now()) {
 const updates = {};
 const issues = [];
 for (const [creator,trips] of Object.entries(data.trips || {})) {
  for (const [id,trip] of Object.entries(trips)) {
   if (trip.createdBy !== creator || trip.id !== id) {issues.push(`Invalid owner/id: ${creator}/${id}`); continue;}
   const existing = data.tripAccess?.[id];
   const members = existing?.members || Object.fromEntries([creator,...Object.values(trip.members || {})].map(uid=>[uid,true]));
   if (existing?.creatorUid && existing.creatorUid !== creator) {issues.push(`Conflicting tripId: ${id}`); continue;}
   updates[`tripAccess/${id}`] = {creatorUid:creator,members,updatedAt:now,revision:(existing?.revision ?? -1)+1};
   for (const uid of Object.keys(members)) updates[`userTrips/${uid}/${id}`] = creator;
  }
 }
 for (const [uid,user] of Object.entries(data.users || {})) {
  if (user.email) updates[`users/${uid}/emailNormalized`] = user.email.trim().toLowerCase();
 }
 for (const [uid,items] of Object.entries(data.favorites || {})) {
  for (const [id,item] of Object.entries(items)) {
   const creator = typeof item === 'string' ? item : item.createdBy;
   const access = updates[`tripAccess/${id}`];
   updates[`favorites/${uid}/${id}`] = creator && access?.members[uid] ? creator : null;
  }
 }
 // Retire stale indexes without deleting trip data.
 for (const [uid,items] of Object.entries(data.userTrips || {})) {
  for (const id of Object.keys(items)) if (!updates[`tripAccess/${id}`]?.members[uid]) updates[`userTrips/${uid}/${id}`] = null;
 }
 return {updates,issues};
}
module.exports = {buildMigration};
