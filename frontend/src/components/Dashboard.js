import { useEffect,useState } from 'react';
import TinderCard from 'react-tinder-card';
import api from '../api';

function Dashboard() {
  const [matches,setMatches]=useState([]);
  const userId=localStorage.getItem('userId');

  useEffect(()=>{
    api.get(`/api/matches/find/${userId}`).then(res=>setMatches(res.data));
  },[userId]);

  const swipe=(dir,user)=>dir==='right'&&api.post(`/api/matches/swipe`,null,{params:{user1Id:userId,user2Id:user.user.id}});

  return <div>{matches.map(m=><TinderCard onSwipe={dir=>swipe(dir,m)} key={m.user.id}><div className="card">{m.user.name}</div></TinderCard>)}</div>;
}

export default Dashboard;