import { useState, useEffect } from 'react';
import axios from 'axios';

function App() {
  const [rooms, setRooms] = useState([]);

  // Hàm lấy danh sách phòng từ Backend của bác
  const fetchRooms = () => {
      axios.get('http://localhost:8080/api/rooms')
        .then(res => setRooms(res.data))
        .catch(err => console.error("Lỗi kết nối Backend: ", err));
  };

  useEffect(() => {
    fetchRooms();
  }, []);

  return (
      <div style={{ padding: '40px', backgroundColor: '#f4f7f6', minHeight: '100vh', fontFamily: 'Segoe UI' }}>
        <h1 style={{ color: '#2c3e50', textAlign: 'center' }}>🏨 QUẢN LÝ KHÁCH SẠN UEH BOUTIQUE</h1>
        <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '30px' }}>
          <button onClick={fetchRooms} style={{ padding: '10px 20px', cursor: 'pointer', borderRadius: '5px', backgroundColor: '#3498db', color: 'white', border: 'none' }}>
            🔄 Làm mới danh sách
          </button>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))', gap: '20px' }}>
          {rooms.map(room => (
              <div key={room.id} style={{
                padding: '20px',
                borderRadius: '12px',
                boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
                textAlign: 'center',
                color: 'white',
                fontWeight: 'bold',
                // Màu sắc theo trạng thái phòng bác đã thiết kế trong Java
                backgroundColor: room.status === 'Empty' ? '#2ecc71' : (room.status === 'Currently' ? '#e74c3c' : '#f1c40f')
              }}>
                <div style={{ fontSize: '24px', marginBottom: '10px' }}>🚪 {room.roomNumber}</div>
                <div style={{ fontSize: '14px', textTransform: 'uppercase' }}>{room.status}</div>
                <div style={{ marginTop: '10px', fontSize: '12px', opacity: 0.8 }}>ID: {room.id}</div>
              </div>
          ))}
        </div>
      </div>
  );
}

export default App;