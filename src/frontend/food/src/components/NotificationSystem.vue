<template>
    <div class="notification-system">
        <div v-for="
        notification in notifications"
        :id="`notification-${notification.id}`"
        :key="notification.id"
        class="notification"
        :class="notification.type">
            {{ notification.message }}
            <div class="progress-bar">
              <div
                class="progress"
                :class="notification.type"
                :id="`progress-${notification.id}`"
                :style="{width: `${notification.time}%`}"></div>
            </div>
            <button @click="removeNotification(notification.id)" class="delete"></button>
        </div>
    </div>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'NotificationSystem',
  data() {
    return {
      notificationTimeSeconds: 10,
    };
  },
  methods: {
    removeNotification(id) {
      this.$store.commit('removeNotification', id);
    },
    notificationTimer(notification) {
      // detect if notification was removed
      if (this.notifications.filter((n) => n.id === notification.id).length === 0) {
        return;
      }
      const updatedNotification = notification;
      // update notification time 100 times per this is more than 60fps so it should be smooth
      const notificationDiv = document.getElementById(`notification-${notification.id}`);
      if (notificationDiv) {
        if (!notificationDiv.matches(':hover')) {
          updatedNotification.time -= 100 / (this.notificationTimeSeconds * 100);
          this.$store.commit('updateNotification', updatedNotification);
        }
      }
      if (updatedNotification.time <= 0) {
        this.removeNotification(updatedNotification.id);
        return;
      }
      setTimeout(() => {
        this.notificationTimer(updatedNotification);
      }, 10); // 100 times per second as obove 1000ms / 10 = ms
    },
  },
  computed: {
    notifications() {
      return this.$store.state.notifications.slice(); // only with slice the watcher triggers on insert see https://stackoverflow.com/questions/71338370/vuejs-v3-how-would-i-watch-for-array-mutations-inserted-removed-replaced-o
    },
  },
  watch: {
    notifications(newArray, oldArray) {
      if (newArray.length > oldArray.length) {
        const newNotification = newArray[newArray.length - 1];
        this.notificationTimer(newNotification);
      }
    },
  },
  mounted() {
    // restart all notification timers on page reload
    this.notifications.forEach((notification) => {
      this.notificationTimer(notification);
    });
  },
});

</script>

<style lang="scss">

.notification-system {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 100;
  width: 300px;
  .notification {
    background-color: #fff;
    border: 1px solid #ccc;
    border-radius: 5px;
    padding: 10px;
    margin-bottom: 10px;
    z-index: 1000;
    position: relative;
    &.success {
      border-color: green;
    }
    &.error {
      border-color: #f44336;
    }
    &.warning {
      border-color: #ffeb3b;
    }
    .delete {
      position: absolute;
      top: 5px;
      right: 5px;
      background-color: transparent;
      border: none;
      cursor: pointer;
      &:before {
        content: '×';
      }
    }
  }
}
.progress-bar {
  background-color: #f1f1f1;
  height: 10px;
  border-radius: 5px;
  margin-top: 5px;
  width: 100%;
  .progress {
    background-color: black;
    height: 100%;
    border-radius: 5px;
    &.success {
      background-color: green;
    }
    &.error {
      background-color: #f44336;
    }
    &.warning {
      background-color: #ffeb3b;
    }
  }
}
</style>
