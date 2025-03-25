import { useEffect,useState } from 'react';
import api from '../api';
import { auth } from '../firebase';

function Profile(){
  const [user,setUser]=useState({});
  const userId=localStorage.getItem('userId');

  useEffect(()=>{api.get(`/api/user/${userId}`).then(res=>setUser(res.data));},[userId]);

  const updatePref=()=>api.post(`/api/user/${userId}/preference`,user.preference).then(()=>alert('Updated'));

  const delAccount=()=>auth.currentUser.getIdToken().then(token=>api.delete('/api/auth/delete',{data:{email:user.email,firebaseToken:token}}).then(()=>alert('Deleted')));

  return user?<div>
    <h3>{user.name}</h3>
    <input placeholder="Major" defaultValue={user.preference?.major} onChange={e=>user.preference.major=e.target.value}/>
    <button onClick={updatePref}>Update Pref</button>
    <button onClick={delAccount}>Delete Account</button>
  </div>:'Loading...';
}

export default Profile;