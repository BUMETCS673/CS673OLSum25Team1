import { useState, useRef } from 'react';
import { styled } from '@mui/material/styles';
import imageCompression from 'browser-image-compression';
import { Snackbar, Alert } from '@mui/material';
import { useAuth } from '../contexts/AuthContext';

const AvatarContainer = styled('div')({
  position: 'relative',
  cursor: 'pointer',
  '&:hover .avatar-overlay': {
    opacity: 1,
  },
});

const AvatarImage = styled('div')({
  width: '40px',
  height: '40px',
  borderRadius: '50%',
  backgroundColor: '#3b82f6',
  color: 'white',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  fontWeight: 'bold',
  fontSize: '1.1rem',
  overflow: 'hidden',
  '& img': {
    width: '100%',
    height: '100%',
    objectFit: 'cover',
  },
});

const AvatarOverlay = styled('div')({
  position: 'absolute',
  top: 0,
  left: 0,
  right: 0,
  bottom: 0,
  backgroundColor: 'rgba(0, 0, 0, 0.5)',
  borderRadius: '50%',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  color: 'white',
  fontSize: '0.8rem',
  opacity: 0,
  transition: 'opacity 0.2s',
});

const HiddenInput = styled('input')({
  display: 'none',
});

export default function AvatarUpload({ user, setUser }) {
  const { updateAvatar } = useAuth();
  const [notification, setNotification] = useState({
    open: false,
    message: '',
    severity: 'success',
  });
  const fileInputRef = useRef(null);

  const handleFileSelect = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    if (!file.type.match(/^image\/(jpeg|png)$/)) {
      setNotification({
        open: true,
        message: '只支持 JPEG 和 PNG 格式的图片',
        severity: 'error',
      });
      return;
    }

    try {
      const options = {
        maxSizeMB: 3,
        maxWidthOrHeight: 1024,
        useWebWorker: true,
      };
      const compressedFile = await imageCompression(file, options);
      
      const reader = new FileReader();
      reader.onload = async (e) => {
        const base64Data = e.target.result;
        try {
          await updateAvatar(base64Data);
          setNotification({
            open: true,
            message: 'success to update avatar',
            severity: 'success',
          });
        } catch (error) {
          setNotification({
            open: true,
            message: error.message || 'failed to update avatar',
            severity: 'error',
          });
        }
      };
      reader.readAsDataURL(compressedFile);
    } catch (error) {
      setNotification({
        open: true,
        message: 'failed to process image',
        severity: 'error',
      });
    }
  };

  const handleClose = (event, reason) => {
    if (reason === 'clickaway') return;
    setNotification({ ...notification, open: false });
  };

  const handleClick = () => {
    fileInputRef.current?.click();
  };

  return (
    <>
      <AvatarContainer onClick={handleClick}>
        <AvatarImage>
          {user.avatar ? (
            <img src={user.avatar} />
          ) : (
            user.username.charAt(0).toUpperCase()
          )}
        </AvatarImage>
        <AvatarOverlay className="avatar-overlay">
          update avatar
        </AvatarOverlay>
        <HiddenInput
          type="file"
          ref={fileInputRef}
          accept="image/jpeg,image/png"
          onChange={handleFileSelect}
        />
      </AvatarContainer>
      <Snackbar
        open={notification.open}
        autoHideDuration={5000}
        onClose={handleClose}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert onClose={handleClose} severity={notification.severity} variant="filled">
          {notification.message}
        </Alert>
      </Snackbar>
    </>
  );
}