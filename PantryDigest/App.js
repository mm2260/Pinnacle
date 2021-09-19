import { StatusBar } from "expo-status-bar";
import React from "react";
import { SafeAreaView } from "react-native-safe-area-context";
import { Button, StyleSheet, Text, View } from "react-native";

// Imports for Card
import { View, Text, Image } from "react-native";
import { Card, ListItem, Button, Icon } from "react-native-elements";

// Import for Tab
import { Tab } from "react-native-elements";

export default function App() {
  return (
    // Home Screen Component goes here

    // end Home Screen

    // Grocery List Component goes here
    <SafeAreaView>
      <View style={styles.container}>
        <TextInput
          style={styles.input}
          onChangeText={onChangeText}
          value={text}
          placeholder="Search recipes"
        />
        <Text>Next scheduled grocery run:</Text>
        <Text>09/25/21</Text>

        <Text>SMS Reminders: ON</Text>

        <Text>Enter New Item(s)</Text>
        <Button>+</Button>

        <Card>Item 1</Card>
        <Card>Item 2</Card>
        <Card>Item 3</Card>
        <Card>Item 4</Card>
        <Card>Item 5</Card>
        <Card>Item 6</Card>

        <Text>begin grocery run</Text>
        <Button>Play Button</Button>

        <Tab value={index} onChange={setIndex}>
          <Tab.Item title="home" />
          <Tab.Item title="list" />
          <Tab.Item title="recipes" />
          <Tab.Item title="settings" />
        </Tab>

        <TabView value={index} onChange={setIndex}>
          <TabView.Item style={{ backgroundColor: "red", width: "100%" }}>
            <Text h1>Home</Text>
          </TabView.Item>
          <TabView.Item style={{ backgroundColor: "blue", width: "100%" }}>
            <Text h1>List</Text>
          </TabView.Item>
          <TabView.Item style={{ backgroundColor: "green", width: "100%" }}>
            <Text h1>Recipes</Text>
          </TabView.Item>
          <TabView.Item style={{ backgroundColor: "yellow", width: "100%" }}>
            <Text h1>Settings</Text>
          </TabView.Item>
        </TabView>

        <StatusBar style="auto" />
      </View>
    </SafeAreaView>
  );
}

//npm run android
//npm run web

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    alignItems: "center",
    justifyContent: "center",
  },
});
